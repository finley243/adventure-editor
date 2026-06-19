package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.ProjectLoadListener;
import com.github.finley243.adventureeditor.ui.RecentProjectListener;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectManager {

    private static final boolean OPEN_CONFIG_MENU_ON_NEW_PROJECT = true;
    private static final int RECENT_PROJECTS_MAXIMUM = 5;
    private static final String UNNAMED_PROJECT_NAME = "Unnamed Project";

    private final DataLoader dataLoader;
    private final TemplateRegistry templateRegistry;
    private final PhraseEditorManager phraseEditorManager;
    private final ScriptEditorManager scriptEditorManager;
    private final ConfigMenuManager configMenuManager;
    private final DataManager dataManager;

    private final List<ProjectFile> recentProjects;

    private boolean isProjectLoaded;
    private String loadedProjectPath;
    private Map<String, Map<String, Data>> lastSavedData;
    private Data lastSavedConfigData;
    private Map<String, String> lastSavedPhrases;
    private Map<String, String> lastSavedScripts;

    private final List<RecentProjectListener> recentProjectListeners;
    private final List<ProjectLoadListener> projectLoadListeners;

    public ProjectManager(DataLoader dataLoader, TemplateRegistry templateRegistry, PhraseEditorManager phraseEditorManager, ScriptEditorManager scriptEditorManager, ConfigMenuManager configMenuManager, DataManager dataManager) {
        this.dataLoader = dataLoader;
        this.templateRegistry = templateRegistry;
        this.phraseEditorManager = phraseEditorManager;
        this.scriptEditorManager = scriptEditorManager;
        this.configMenuManager = configMenuManager;
        this.dataManager = dataManager;
        this.recentProjects = new ArrayList<>();
        this.isProjectLoaded = false;
        this.loadedProjectPath = null;
        this.recentProjectListeners = new ArrayList<>();
        this.projectLoadListeners = new ArrayList<>();
    }

    public void registerRecentProjectListener(RecentProjectListener recentProjectListener) {
        this.recentProjectListeners.add(recentProjectListener);
    }

    public void registerProjectLoadListener(ProjectLoadListener projectLoadListener) {
        this.projectLoadListeners.add(projectLoadListener);
    }

    public boolean isProjectLoaded() {
        return isProjectLoaded;
    }

    public boolean isProjectSaved() {
        return isProjectLoaded && loadedProjectPath != null;
    }

    public boolean hasUnsavedChanges() {
        if (!isProjectLoaded()) {
            return false;
        }
        if (!isProjectSaved()) {
            return true;
        }
        if (phraseEditorManager.hasChangesFrom(lastSavedPhrases)) {
            return true;
        }
        if (scriptEditorManager.hasChangesFrom(lastSavedScripts)) {
            return true;
        }
        if (configMenuManager.hasChangesFrom(lastSavedConfigData)) {
            return true;
        }
        return dataManager.hasChangesFrom(lastSavedData);
    }

    public List<ProjectFile> getRecentProjects() {
        return new ArrayList<>(recentProjects);
    }

    public void setRecentProjects(List<ProjectFile> projects) {
        this.recentProjects.clear();
        this.recentProjects.addAll(projects);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
        dataLoader.saveRecentProjects(recentProjects);
        onUpdateRecentProjects(recentProjects);
    }

    public void removeRecentProject(ProjectFile project) {
        recentProjects.remove(project);
        dataLoader.saveRecentProjects(recentProjects);
        onUpdateRecentProjects(recentProjects);
    }

    public void clearRecentProjects() {
        recentProjects.clear();
        dataLoader.saveRecentProjects(recentProjects);
        onUpdateRecentProjects(recentProjects);
    }

    public void updateProjectName() {
        String name = configMenuManager.getProjectName();
        if (name == null && isProjectLoaded()) {
            configMenuManager.onProjectNameChange(UNNAMED_PROJECT_NAME);
        } else {
            configMenuManager.onProjectNameChange(name);
        }
    }

    public void newProject(Window parentWindow, ParameterFactory parameterFactory, MainFrame mainFrame) {
        boolean continueCheck = saveConfirmationIfHasUnsavedData(mainFrame);
        if (!continueCheck) {
            return;
        }
        dataManager.clearData();
        configMenuManager.clearConfigData();
        onLoadProject(templateRegistry.getAllTemplates(), dataManager.getAllData());
        isProjectLoaded = true;
        loadedProjectPath = null;
        updateProjectName();
        if (OPEN_CONFIG_MENU_ON_NEW_PROJECT) {
            configMenuManager.openConfigMenu(parentWindow, parameterFactory);
        }
    }

    public void openProjectFromMenu(MainFrame mainFrame) {
        boolean continueCheck = saveConfirmationIfHasUnsavedData(mainFrame);
        if (!continueCheck) {
            return;
        }
        File selectedDirectory = mainFrame.selectProjectLoadDirectory();
        if (selectedDirectory == null) return;
        dataManager.clearData();
        configMenuManager.clearConfigData();
        phraseEditorManager.clearPhrases();
        scriptEditorManager.clearScripts();
        //try {
            ProjectLoadData projectLoadData = dataLoader.loadFromDir(selectedDirectory, templateRegistry);
            configMenuManager.setConfigData(projectLoadData.configData());
            dataManager.setData(projectLoadData.gameData());
            phraseEditorManager.setPhrases(projectLoadData.phrases());
            scriptEditorManager.setScripts(projectLoadData.scripts());
            onLoadProject(templateRegistry.getAllTemplates(), dataManager.getAllData());
            ProjectFile project = new ProjectFile(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            isProjectLoaded = true;
            loadedProjectPath = selectedDirectory.getAbsolutePath();
            updateLastSavedData();
            updateProjectName();
        /*} catch (ParserConfigurationException | SAXException e) {
            dataManager.clearData();
            configMenuManager.clearConfigData();
            mainFrame.showErrorDialog("The selected project has data that is improperly formed.");
        } catch (IOException e) {
            dataManager.clearData();
            configMenuManager.clearConfigData();
            mainFrame.showErrorDialog("The selected project directory cannot be read.");
        }*/
    }

    public void openRecentProject(ProjectFile projectFile, MainFrame mainFrame) {
        File file = new File(projectFile.absolutePath());
        if (!file.exists()) {
            boolean deleteMissingProject = mainFrame.recentProjectDeleteConfirmation();
            if (deleteMissingProject) {
                removeRecentProject(projectFile);
            }
            return;
        }
        boolean continueCheck = saveConfirmationIfHasUnsavedData(mainFrame);
        if (!continueCheck) {
            return;
        }
        dataManager.clearData();
        configMenuManager.clearConfigData();
        phraseEditorManager.clearPhrases();
        scriptEditorManager.clearScripts();
        //try {
            ProjectLoadData projectData = dataLoader.loadFromDir(file, templateRegistry);
            configMenuManager.setConfigData(projectData.configData());
            onLoadProject(templateRegistry.getAllTemplates(), dataManager.getAllData());
            ProjectFile project = new ProjectFile(file.getName(), file.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            isProjectLoaded = true;
            loadedProjectPath = file.getAbsolutePath();
            updateLastSavedData();
            updateProjectName();
        /*} catch (ParserConfigurationException | SAXException e) {
            dataManager.clearData();
            configMenuManager.clearConfigData();
            mainFrame.showErrorDialog("The selected project has data that is improperly formatted.");
        } catch (IOException e) {
            dataManager.clearData();
            configMenuManager.clearConfigData();
            mainFrame.showErrorDialog("The selected project directory cannot be read.");
        }*/
    }

    public boolean saveProjectToCurrentPath(MainFrame mainFrame) {
        if (loadedProjectPath == null) {
            return saveProjectToMenu(mainFrame);
        } else {
            File loadedDirectory = new File(loadedProjectPath);
            //try {
                dataLoader.saveToDir(loadedDirectory, templateRegistry, dataManager.getAllData(), configMenuManager, scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
                ProjectFile project = new ProjectFile(loadedDirectory.getName(), loadedDirectory.getAbsolutePath());
                addOrMoveRecentProjectToTop(project);
                updateLastSavedData();
                return true;
            /*} catch (IOException e) {
                mainFrame.showErrorDialog("Project could not be saved to the current directory.");
                return false;
            } catch (ParserConfigurationException | TransformerException e) {
                mainFrame.showErrorDialog("Save system encountered an error. Please try again.");
                return false;
            }*/
        }
    }

    public boolean saveProjectToMenu(MainFrame mainFrame) {
        File selectedDirectory = mainFrame.selectProjectSaveDirectory();
        if (selectedDirectory == null) {
            return false;
        }
        //try {
            dataLoader.saveToDir(selectedDirectory, templateRegistry, dataManager.getAllData(), configMenuManager, scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
            ProjectFile project = new ProjectFile(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            loadedProjectPath = selectedDirectory.getAbsolutePath();
            updateLastSavedData();
            return true;
        /*} catch (IOException e) {
            mainFrame.showErrorDialog("Project could not be saved to the selected directory.");
            return false;
        } catch (ParserConfigurationException | TransformerException e) {
            mainFrame.showErrorDialog("Save system encountered an error. Please try again.");
            return false;
        }*/
    }

    public boolean saveConfirmationIfHasUnsavedData(MainFrame mainFrame) {
        if (!hasUnsavedChanges()) {
            return true;
        }
        MainFrame.SaveConfirmationResult result = mainFrame.projectSaveConfirmation();
        if (result == MainFrame.SaveConfirmationResult.YES) {
            return saveProjectToCurrentPath(mainFrame);
        } else {
            return result == MainFrame.SaveConfirmationResult.NO;
        }
    }

    private void addOrMoveRecentProjectToTop(ProjectFile project) {
        recentProjects.remove(project);
        recentProjects.addFirst(project);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
        dataLoader.saveRecentProjects(recentProjects);
        onUpdateRecentProjects(recentProjects);
    }

    private void updateLastSavedData() {
        lastSavedData = dataManager.getAllDataCopy();
        lastSavedConfigData = configMenuManager.getConfigData().createCopy();
        lastSavedPhrases = new HashMap<>(phraseEditorManager.getPhrases());
        lastSavedScripts = new HashMap<>(scriptEditorManager.getScripts());
    }

    private void onUpdateRecentProjects(List<ProjectFile> recentProjects) {
        for (RecentProjectListener listener : recentProjectListeners) {
            listener.onUpdateRecentProjects(recentProjects);
        }
    }

    private void onLoadProject(Map<String, Template> templates, Map<String, Map<String, Data>> loadedData) {
        for (ProjectLoadListener listener : projectLoadListeners) {
            listener.onLoadProject(templates, loadedData);
        }
    }

}
