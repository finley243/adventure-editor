package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;

import java.util.*;
import java.util.List;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        DataLoader dataLoader = new DataLoader();
        Map<String, Template> templateMap = dataLoader.loadTemplates();
        Map<String, List<String>> enumTypeMap = dataLoader.loadEnumTypes();
        TemplateRegistry templateRegistry = new TemplateRegistry(templateMap, enumTypeMap);
        List<ProjectFile> recentProjects = dataLoader.loadRecentProjects();
        PhraseEditorManager phraseEditorManager = new PhraseEditorManager();
        ScriptEditorManager scriptEditorManager = new ScriptEditorManager();
        ConfigMenuManager configMenuManager = new ConfigMenuManager();
        EditorManager editorManager = new EditorManager();
        MainFrame mainFrame = new MainFrame();
        BrowserFrame browserFrame = new BrowserFrame(mainFrame);
        ReferenceListManager referenceListManager = new ReferenceListManager(browserFrame);
        DataManager dataManager = new DataManager(editorManager, templateRegistry, referenceListManager, configMenuManager);
        ProjectManager projectManager = new ProjectManager(dataLoader, phraseEditorManager, scriptEditorManager, configMenuManager, dataManager);
        projectManager.setRecentProjects(recentProjects);
    }

}