package com.example.yapi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class YapiToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 在这里创建工具窗口的内容
        YapiToolWindowContent yapiContent = new YapiToolWindowContent();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(yapiContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
