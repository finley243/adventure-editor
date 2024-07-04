package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.browser.MainFrame;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

public class Main {

    private final Map<String, Template> templates;
    private final Map<String, List<String>> enumTypes;

    private final ConfigMenuManager configMenuManager;
    private final ProjectManager projectManager;
    private final DataManager dataManager;
    private final PhraseEditorManager phraseEditorManager;

    private final MainFrame mainFrame;
    private final BrowserFrame browserFrame;

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Main main = new Main();
    }

    public Main() throws ParserConfigurationException, IOException, SAXException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.templates = new HashMap<>();
        this.enumTypes = new HashMap<>();
        this.configMenuManager = new ConfigMenuManager(this);
        this.projectManager = new ProjectManager(this);
        this.dataManager = new DataManager(this);
        this.mainFrame = new MainFrame(this);
        this.browserFrame = new BrowserFrame(this, mainFrame);
        this.phraseEditorManager = new PhraseEditorManager(this);
        initialLoad();
    }

    private void initialLoad() throws ParserConfigurationException, IOException, SAXException {
        DataLoader.loadTemplates(templates, enumTypes);
        List<ProjectData> recentProjects = new ArrayList<>();
        DataLoader.loadRecentProjects(recentProjects);
        projectManager.setRecentProjects(recentProjects);
    }

    public ConfigMenuManager getConfigMenuManager() {
        return configMenuManager;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public BrowserFrame getBrowserFrame() {
        return browserFrame;
    }

    public PhraseEditorManager getPhraseEditorManager() {
        return phraseEditorManager;
    }

    public Template getTemplate(String categoryID) {
        return templates.get(categoryID);
    }

    public Map<String, Template> getAllTemplates() {
        return templates;
    }

    public List<String> getEnumValues(String enumID) {
        return enumTypes.get(enumID);
    }

}