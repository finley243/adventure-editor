package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

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
        ConfigMenuManager configMenuManager = new ConfigMenuManager(templateRegistry.getConfigTemplate());
        EditorManager editorManager = new EditorManager();
        MainFrame mainFrame = new MainFrame();
        DataManager dataManager = new DataManager(editorManager, templateRegistry, referenceListManager, configMenuManager);
        TopLevelSaveTarget topLevelSaveTarget = new TopLevelSaveTarget(dataManager, editorManager);
        ReferenceListManager referenceListManager = new ReferenceListManager(configMenuManager, dataManager, topLevelSaveTarget);
        ParameterFactory parameterFactory = new ParameterFactory(templateRegistry, dataManager, topLevelSaveTarget);
        BrowserFrame browserFrame = new BrowserFrame(mainFrame, editorManager, dataManager, topLevelSaveTarget, parameterFactory);
        dataManager.registerObjectUpdateListener(browserFrame);
        dataManager.registerCategoryUpdateListener(browserFrame);
        ProjectManager projectManager = new ProjectManager(dataLoader, templateRegistry, phraseEditorManager, scriptEditorManager, configMenuManager, dataManager);
        projectManager.registerProjectLoadListener(browserFrame);
        projectManager.registerRecentProjectListener(mainFrame);
        projectManager.setRecentProjects(recentProjects);
    }

}