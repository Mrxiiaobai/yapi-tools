package com.optimus.yapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaToJson {
    public String convert(VirtualFile currentFile, Project project) {
        String str = null;
        if (currentFile != null && currentFile.getFileType().getName().equals("JAVA")) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(currentFile);

            if (psiJavaFile != null) {
                // 获取当前打开文件的类信息
                PsiClass[] classes = psiJavaFile.getClasses();

                JSONObject parentObject = new JSONObject();

                parentObject.put("type", "object");
                parentObject.put("title", "empty object");
                parentObject.put("properties", "empty object");

                // 创建一个空的 JSONObject
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                if (classes.length > 0) {
                    PsiClass currentClass = classes[0];
//                    String classPath = project.getBasePath();
//                    String className = currentClass.getQualifiedName();

                    try {
                        PsiField[] fields = currentClass.getFields();
                        for(PsiField field : fields){
                            // 生成 Schema
                            JSONObject propertyObject = new JSONObject();

                            String name = field.getName();
                            String comment = getCommentText(field);
                            String typeName = getTypeName(field.getType().getPresentableText(), propertyObject);
                            Boolean ifRequired = findAnnotationsForField(field);

                            if(ifRequired) {
                                jsonArray.add(name);
                            }

                            propertyObject.put("type", typeName);
                            propertyObject.put("description", comment);

                            jsonObject.put(name, propertyObject);
                        }

                        parentObject.put("properties", jsonObject);
                        parentObject.put("required", jsonArray);

                        System.out.println("parentObject"+ parentObject);

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        str = gson.toJson(parentObject);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return str;
    }

    /** 获取注释 */
    private String getCommentText(PsiField field){
        PsiDocComment comment = field.getDocComment();
        String result = "";
        if (comment != null) {
            String commentText = comment.getText();
            // 使用正则表达式定义匹配模式
            String regex = "/\\*\\*(.*?)\\*/"; // 匹配以 "/**" 开头，以 "*/" 结尾的多行注释，并捕获中间的内容

            // 使用正则表达式匹配注释文本
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(commentText);

            // 获取匹配到的文本
            if (matcher.find()) {
                String tempStr = matcher.group(1).trim(); // 获取第一个分组的内容（即中间的文字），并去除首尾空格
                // 去除星号
                result = tempStr.replace("*", "").trim();
            }
        }
        return result;
    }

    /** 获取转换后的类型 */
    private String getTypeName(String typeName, JSONObject propertyObject){
        String name = typeName.toLowerCase();

        switch (name) {
            case "long", "int", "number", "integer" -> name = "number";
            case "array" -> name = "array";
            case "boolean" -> name = "boolean";
            case "string" -> name = "string";
            case "object" -> name = "object";
            default -> {
                if (name.contains("list")) {
                    name = "array"; // 提取 list 类型
                    JSONObject childObject = new JSONObject();
                    String childTypeName = typeName.toLowerCase().replace("list<", "").replace(">", "");
                    childObject.put("type", getTypeName(childTypeName, childObject));
                    propertyObject.put("items", childObject);
                } else {
                    name = "object";
                }
            }
            // 默认情况处理
        }
        return name;
    }

    private Boolean findAnnotationsForField(PsiField field) {
        List<PsiAnnotation> annotations = new ArrayList<>(Arrays.asList(field.getAnnotations()));
        boolean result = false;
        for (PsiAnnotation annotation : annotations) {
            String qualifiedName = annotation.getQualifiedName();
            if ("NotBlank".equals(qualifiedName) || "NotNull".equals(qualifiedName)) {
                result =  true;
            }
        }
        return result;
    }
}

