package com.example.yapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

public class YapiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        // 获取当前项目
        Project project = e.getProject();

        // 获取当前选中的文件
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile != null && project != null) {
            // 确保是 Java 文件
            if (virtualFile.getFileType().getDefaultExtension().equals("java")) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile != null) {
                    // 获取文件代码
                    String fileContent = psiFile.getText();
                    System.out.println("当前打开的 Java 文件代码：\n" + fileContent);

                    showCodeInToolWindow(fileContent, project);
                }
            }
        }
    }

    private void showCodeInToolWindow(String code, Project project) {
        String jsonCode = JavaScriptToJsonConverter.javaToJson(code);
        JComponent codeComponent = new JTextArea(jsonCode);
        JScrollPane scrollPane = new JBScrollPane(codeComponent);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("yapiWindow");
        if (toolWindow != null) {
            toolWindow.show(null);
        }
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(
            ContentFactory.getInstance().createContent(scrollPane, "", false)
        );
    }
}
