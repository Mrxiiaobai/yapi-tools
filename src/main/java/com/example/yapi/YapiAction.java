package com.example.yapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

public class YapiAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        if (project != null) {
            // 获取当前编辑的文件
            VirtualFile currentFile = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
            JavaToJson javaToJson = new JavaToJson();
            String jsonCode = javaToJson.convert(currentFile, project);
            showCodeInToolWindow(jsonCode, project);
        }
    }



    private void showCodeInToolWindow(String jsonCode, Project project) {
        JComponent codeComponent = new JTextArea(jsonCode);
        JScrollPane scrollPane = new JBScrollPane(codeComponent);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("yapi");
        if (toolWindow != null) {
            toolWindow.show(null);
            toolWindow.getContentManager().removeAllContents(true);
            toolWindow.getContentManager().addContent(
                    ContentFactory.getInstance().createContent(scrollPane, "", false)
            );
        }
    }
}

