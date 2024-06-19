package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.ProjectData;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private static final boolean OPEN_CONFIG_MENU_ON_NEW_PROJECT = true;
    private static final String UNNAMED_PROJECT_NAME = "Unnamed Project";

    private final Main main;
    private final List<ProjectData> recentProjects;

    private boolean isProjectLoaded;
    private String loadedProjectPath;

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
        return isProjectLoaded && loadedProjectPath == null;
    }

    public List<ProjectData> getRecentProjects() {
        return recentProjects;
    }

    public void setRecentProjects(List<ProjectData> recentProjects) {
        this.recentProjects.clear();
        this.recentProjects.addAll(recentProjects);
        main.getBrowserFrame().updateRecentProjects();
    }

    public void updateProjectName() {
        String configProjectName = main.getConfigMenuHandler().getProjectName();
        if (configProjectName == null && isProjectLoaded()) {
            main.getBrowserFrame().setProjectName(UNNAMED_PROJECT_NAME);
        } else {
            main.getBrowserFrame().setProjectName(configProjectName);
        }
    }

    public void newProject() {
        // TODO - Add save confirmation if a project is open
        main.getDataManager().clearData();
        main.getConfigMenuHandler().clearConfigData();
        main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
        isProjectLoaded = true;
        loadedProjectPath = null;
        updateProjectName();
        if (OPEN_CONFIG_MENU_ON_NEW_PROJECT) {
            main.openConfigMenu();
        }
    }

    public void openProjectFromMenu() {
        // TODO - Add save confirmation if a project is open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(main.getBrowserFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        main.getDataManager().clearData();
        main.getConfigMenuHandler().clearConfigData();
        try {
            DataLoader.loadFromDir(selectedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuHandler());
            main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            main.getBrowserFrame().updateRecentProjects();
            isProjectLoaded = true;
            loadedProjectPath = selectedDirectory.getAbsolutePath();
            updateProjectName();
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuHandler().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuHandler().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openProjectFromFile(File file) {
        // TODO - Add save confirmation if a project is open
        main.getDataManager().clearData();
        main.getConfigMenuHandler().clearConfigData();
        try {
            DataLoader.loadFromDir(file, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuHandler());
            main.getBrowserFrame().reloadBrowserData(main.getAllTemplates(), main.getDataManager().getAllData());
            isProjectLoaded = true;
            loadedProjectPath = file.getAbsolutePath();
            updateProjectName();
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuHandler().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            main.getDataManager().clearData();
            main.getConfigMenuHandler().clearConfigData();
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveProjectToCurrentPath() {
        if (loadedProjectPath == null) {
            saveProjectToMenu();
        } else {
            File loadedDirectory = new File(loadedProjectPath);
            try {
                DataLoader.saveToDir(loadedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuHandler());
                ProjectData project = new ProjectData(loadedDirectory.getName(), loadedDirectory.getAbsolutePath());
                recentProjects.remove(project);
                recentProjects.addFirst(project);
                main.getBrowserFrame().updateRecentProjects();
            } catch (IOException e) {
                //throw new RuntimeException(e);
                JOptionPane.showMessageDialog(main.getBrowserFrame(), "Project could not be saved to the current directory.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException | TransformerException e) {
                //throw new RuntimeException(e);
                JOptionPane.showMessageDialog(main.getBrowserFrame(), "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveProjectToMenu() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(main.getBrowserFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        try {
            DataLoader.saveToDir(selectedDirectory, main.getAllTemplates(), main.getDataManager().getAllData(), main.getConfigMenuHandler());
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            main.getBrowserFrame().updateRecentProjects();
            loadedProjectPath = selectedDirectory.getAbsolutePath();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "Project could not be saved to the selected directory.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParserConfigurationException | TransformerException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(main.getBrowserFrame(), "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
