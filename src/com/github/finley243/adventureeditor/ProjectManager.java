package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private static final boolean OPEN_CONFIG_MENU_ON_NEW_PROJECT = true;
    private static final int RECENT_PROJECTS_MAXIMUM = 5;
    private static final String UNNAMED_PROJECT_NAME = "Unnamed Project";

    private final List<ProjectFile> recentProjects;

    private boolean isProjectLoaded;
    private String loadedProjectPath;

    public ProjectManager() {
        this.recentProjects = new ArrayList<>();
        this.isProjectLoaded = false;
        this.loadedProjectPath = null;
    }

    public void setProjectLoaded(boolean loaded) {
        this.isProjectLoaded = loaded;
    }

    public boolean hasUnsavedChanges() {
        if (!isProjectLoaded) {
            return false;
        }
        return loadedProjectPath == null;
    }

    public void addRecentProject(ProjectFile project) {
        recentProjects.remove(project);
        recentProjects.addFirst(project);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
    }

    public void loadRecentProjects(List<ProjectFile> projects) {
        recentProjects.clear();
        recentProjects.addAll(projects);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
    }

    public List<ProjectFile> getRecentProjects() {
        return new ArrayList<>(recentProjects);
    }

    public void removeRecentProject(ProjectFile project) {
        recentProjects.remove(project);
    }

    public void clearRecentProjects() {
        recentProjects.clear();
    }

    public void setLoadedProjectPath(String path) {
        this.loadedProjectPath = path;
    }

    public String getLoadedProjectPath() {
        return loadedProjectPath;
    }

    /*public void newProject(Window parentWindow, ParameterFactory parameterFactory, MainFrame mainFrame) {
        boolean continueCheck = saveConfirmationIfHasUnsavedData(mainFrame);
        if (!continueCheck) {
            return;
        }
        dataManager.clearData();
        configMenuManager.clearConfigData();
        phraseEditorManager.clearPhrases();
        scriptEditorManager.clearScripts();
        onLoadProject(templateRegistry.getAllTemplates(), dataManager.getAllData());
        isProjectLoaded = true;
        loadedProjectPath = null;
        updateLastSavedData();
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
        ProjectLoadData projectData = dataLoader.loadFromDir(file, templateRegistry);
        configMenuManager.setConfigData(projectData.configData());
        dataManager.setData(projectData.gameData());
        phraseEditorManager.setPhrases(projectData.phrases());
        scriptEditorManager.setScripts(projectData.scripts());
        onLoadProject(templateRegistry.getAllTemplates(), dataManager.getAllData());
        ProjectFile project = new ProjectFile(file.getName(), file.getAbsolutePath());
        addOrMoveRecentProjectToTop(project);
        isProjectLoaded = true;
        loadedProjectPath = file.getAbsolutePath();
        updateLastSavedData();
        updateProjectName();
    }

    public boolean saveProjectToCurrentPath(MainFrame mainFrame) {
        if (loadedProjectPath == null) {
            return saveProjectToMenu(mainFrame);
        } else {
            File loadedDirectory = new File(loadedProjectPath);
            dataLoader.saveToDir(loadedDirectory, templateRegistry, dataManager.getAllData(), configMenuManager, scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
            ProjectFile project = new ProjectFile(loadedDirectory.getName(), loadedDirectory.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            updateLastSavedData();
            return true;
        }
    }

    public boolean saveProjectToMenu(MainFrame mainFrame) {
        File selectedDirectory = mainFrame.selectProjectSaveDirectory();
        if (selectedDirectory == null) {
            return false;
        }
        dataLoader.saveToDir(selectedDirectory, templateRegistry, dataManager.getAllData(), configMenuManager, scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
        ProjectFile project = new ProjectFile(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
        addOrMoveRecentProjectToTop(project);
        loadedProjectPath = selectedDirectory.getAbsolutePath();
        updateLastSavedData();
        return true;
    }*/

}
