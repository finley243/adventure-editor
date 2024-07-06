package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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

    private final Main main;
    private final List<ProjectData> recentProjects;

    private boolean isProjectLoaded;
    private String loadedProjectPath;
    private Map<String, Map<String, Data>> lastSavedData;
    private Data lastSavedConfigData;
    private Map<String, String> lastSavedPhrases;

    public ProjectManager(Main main) {
        this.main = main;
        this.recentProjects = new ArrayList<>();
        this.isProjectLoaded = false;
        this.loadedProjectPath = null;
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
        if (main.getPhraseEditorManager().hasChangesFrom(lastSavedPhrases)) {
            return true;
        }
        if (main.getConfigMenuManager().hasChangesFrom(lastSavedConfigData)) {
            return true;
        }
        return main.getDataManager().hasChangesFrom(lastSavedData);
    }

    public List<ProjectData> getRecentProjects() {
        return new ArrayList<>(recentProjects);
    }

    public void setRecentProjects(List<ProjectData> projects) {
        this.recentProjects.clear();
        this.recentProjects.addAll(projects);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
        DataLoader.saveRecentProjects(recentProjects);
        main.getMainFrame().updateRecentProjects();
    }

    public void removeRecentProject(ProjectData project) {
        recentProjects.remove(project);
        DataLoader.saveRecentProjects(recentProjects);
        main.getMainFrame().updateRecentProjects();
    }

    public void clearRecentProjects() {
        recentProjects.clear();
        DataLoader.saveRecentProjects(recentProjects);
        main.getMainFrame().updateRecentProjects();
    }

    public void updateProjectName() {
        String configProjectName = main.getConfigMenuManager().getProjectName();
        if (configProjectName == null && isProjectLoaded()) {
            main.getMainFrame().setProjectName(UNNAMED_PROJECT_NAME);
        } else {
            main.getMainFrame().setProjectName(configProjectName);
        }
    }

    public void newProject() {
        boolean continueCheck = saveConfirmationIfHasUnsavedData();
        if (!continueCheck) {
            return;
        }
        main.getDataManager().clearData();
        main.getConfigMenuManager().clearConfigData();
        main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
        isProjectLoaded = true;
        loadedProjectPath = null;
        updateProjectName();
        if (OPEN_CONFIG_MENU_ON_NEW_PROJECT) {
            main.getConfigMenuManager().openConfigMenu();
        }
    }

    public void openProjectFromMenu() {
        boolean continueCheck = saveConfirmationIfHasUnsavedData();
        if (!continueCheck) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(main.getBrowserFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        main.getDataManager().clearData();
        main.getConfigMenuManager().clearConfigData();
        try {
            DataLoader.loadFromDir(selectedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuManager(), new HashMap<>(), main.getPhraseEditorManager().getPhrases());
            main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            isProjectLoaded = true;
            loadedProjectPath = selectedDirectory.getAbsolutePath();
            updateLastSavedData();
            updateProjectName();
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuManager().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuManager().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openRecentProject(ProjectData projectData) {
        File file = new File(projectData.absolutePath());
        if (!file.exists()) {
            int choice = JOptionPane.showOptionDialog(main.getBrowserFrame(), "The selected project file was not found. Remove it from recent projects?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Yes", "No"}, "No");
            if (choice == JOptionPane.YES_OPTION) {
                removeRecentProject(projectData);
            }
            return;
        }
        boolean continueCheck = saveConfirmationIfHasUnsavedData();
        if (!continueCheck) {
            return;
        }
        main.getDataManager().clearData();
        main.getConfigMenuManager().clearConfigData();
        try {
            DataLoader.loadFromDir(file, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuManager(), new HashMap<>(), main.getPhraseEditorManager().getPhrases());
            main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
            ProjectData project = new ProjectData(file.getName(), file.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            isProjectLoaded = true;
            loadedProjectPath = file.getAbsolutePath();
            updateLastSavedData();
            updateProjectName();
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuManager().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuManager().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean saveProjectToCurrentPath() {
        if (loadedProjectPath == null) {
            return saveProjectToMenu();
        } else {
            File loadedDirectory = new File(loadedProjectPath);
            try {
                DataLoader.saveToDir(loadedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuManager(), main.getPhraseEditorManager().getPhrases());
                ProjectData project = new ProjectData(loadedDirectory.getName(), loadedDirectory.getAbsolutePath());
                addOrMoveRecentProjectToTop(project);
                updateLastSavedData();
                return true;
            } catch (IOException e) {
                //throw new RuntimeException(e);
                JOptionPane.showMessageDialog(main.getBrowserFrame(), "Project could not be saved to the current directory.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            } catch (ParserConfigurationException | TransformerException e) {
                //throw new RuntimeException(e);
                JOptionPane.showMessageDialog(main.getBrowserFrame(), "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    public boolean saveProjectToMenu() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(main.getBrowserFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        try {
            DataLoader.saveToDir(selectedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuManager(), main.getPhraseEditorManager().getPhrases());
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            addOrMoveRecentProjectToTop(project);
            loadedProjectPath = selectedDirectory.getAbsolutePath();
            updateLastSavedData();
            return true;
        } catch (IOException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "Project could not be saved to the selected directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (ParserConfigurationException | TransformerException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean saveConfirmationIfHasUnsavedData() {
        if (!hasUnsavedChanges()) {
            return true;
        }
        int result = JOptionPane.showConfirmDialog(main.getBrowserFrame(), "Save changes to the current project?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            return saveProjectToCurrentPath();
        } else {
            return result == JOptionPane.NO_OPTION;
        }
    }

    private void addOrMoveRecentProjectToTop(ProjectData project) {
        recentProjects.remove(project);
        recentProjects.addFirst(project);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
        DataLoader.saveRecentProjects(recentProjects);
        main.getMainFrame().updateRecentProjects();
    }

    private void updateLastSavedData() {
        lastSavedData = main.getDataManager().getAllDataCopy();
        lastSavedConfigData = main.getConfigMenuManager().getConfigData().createCopy();
        lastSavedPhrases = new HashMap<>(main.getPhraseEditorManager().getPhrases());
    }

}
