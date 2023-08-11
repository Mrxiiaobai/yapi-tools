package com.optimus.yapi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class YapiToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        // 在这里创建工具窗口的内容
        YapiToolWindowContent yapiContent = new YapiToolWindowContent();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(yapiContent, "", false);
//        EditorFactory editorFactory = EditorFactory.getInstance();
//        Document content = editorFactory.createDocument((CharSequence) yapiContent);
        toolWindow.getContentManager().addContent((Content) content);
    }
}
