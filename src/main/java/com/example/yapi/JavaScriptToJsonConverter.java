package com.example.yapi;

import net.minidev.json.JSONObject;

import java.util.*;

public class JavaScriptToJsonConverter {
    public static String javaToJson(String code) {
        return convertToJsonObject(code);
    }
    public static String convertToJsonObject(String str) {
        JSONObject jsonObject = new JSONObject();
        StringBuilder comment = null; // 记录变量定义前的注释信息

        String[] arr = str.split("\n");

        List<String> javaCode = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].contains("public")) {
                javaCode = Arrays.asList(arr).subList(i, arr.length);
            }
        }

        for (int i=0;i<javaCode.size(); i++){
            String prop = javaCode.get(i);
            String newProp = prop.trim();
            if (newProp.startsWith("//")) {
                comment = new StringBuilder();
                comment.append(newProp.substring(2).trim()).append(" ");
            } else if (newProp.startsWith("/*")) {
                StringBuilder strs = new StringBuilder();
                while (!newProp.endsWith("*/") && i < javaCode.size() - 1) {
                    strs.append(javaCode.get(i));
                    System.out.println("javaCode.get(i)"+javaCode.get(i));
                    System.out.println("strs:1"+strs);
                    newProp = javaCode.get(++i).trim();
                }

                strs.append("*/'");
                int startIdx = strs.indexOf("/*");
                int endIdx = strs.indexOf("*/", startIdx);
                String text = strs.substring(startIdx + 1, endIdx).trim();
                String result = text.replaceAll("\\*", "").trim();
                comment = new StringBuilder();
                comment.append(result).append(" ");
            } else if (newProp.startsWith("private ")) {
                String[] keyVal = newProp.split(" ");
                String key = keyVal[keyVal.length - 1].substring(0, keyVal[keyVal.length - 1].length() - 1);

                Map<String, String> normalTypes = new HashMap<>();
                normalTypes.put("long", "number");
                normalTypes.put("int", "number");
                normalTypes.put("integer", "number");

                Map<String, String> types = new HashMap<>(normalTypes);

                String value = keyVal[1].toLowerCase();

                String normalValue = types.getOrDefault(value, value);
                boolean specialValue = value.contains("list");

                String type = specialValue ? "array" : normalValue;
                Map<String, Object> params = new HashMap<>();
                params.put("type", type);
                params.put("description", comment != null ? comment.toString().trim() : "");

                Map<String, String> specialType = new HashMap<>();
                specialType.put("list<string>", "array");
                specialType.put("list<object>", "array");

                for (Map.Entry<String, String> entry : specialType.entrySet()) {
                    if (entry.getKey().equals(value)) {
                        Map<String, Object> items = new HashMap<>();
                        items.put("type", entry.getKey().replace("list<", "").replace(">", ""));
                        items.put("properties", new HashMap<>());
                        params.put("items", items);
                    }
                }

                jsonObject.put(key, params);

                comment = null; // 注意重置 comment 变量
            }
        }

        Map<String, Object> resultJson = new HashMap<>();
        resultJson.put("type", "object");
        resultJson.put("title", "empty object");
        resultJson.put("properties", jsonObject);

        return new JSONObject(resultJson).toString();
    }
}

