package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.ProjectData;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main {

    private final Map<String, Template> templates;
    private final Map<String, List<String>> enumTypes;
    private final List<ProjectData> recentProjects;
    private final Map<String, Map<String, Data>> data;

    private final ConfigMenuHandler configMenuHandler;

    private final BrowserFrame browserFrame;

    private boolean isProjectLoaded;

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
        this.recentProjects = new ArrayList<>();
        this.configMenuHandler = new ConfigMenuHandler(this);
        this.browserFrame = new BrowserFrame(this);
        DataLoader.loadTemplates(templates, enumTypes);
        DataLoader.loadRecentProjects(recentProjects);
        browserFrame.updateRecentProjects();
        this.data = new HashMap<>();
        isProjectLoaded = false;
    }

    public boolean isProjectLoaded() {
        return isProjectLoaded;
    }

    public ConfigMenuHandler getConfigMenuHandler() {
        return configMenuHandler;
    }

    public List<ProjectData> getRecentProjects() {
        return recentProjects;
    }

    public Template getTemplate(String categoryID) {
        return templates.get(categoryID);
    }

    public List<String> getEnumValues(String enumID) {
        return enumTypes.get(enumID);
    }

    public Set<String> getIDsForCategory(String categoryID) {
        if (!data.containsKey(categoryID)) {
            return null;
        }
        return data.get(categoryID).keySet();
    }

    public Data getData(String categoryID, String objectID) {
        if (data.get(categoryID) == null) {
            return null;
        }
        return data.get(categoryID).get(objectID);
    }

    public void saveObjectData(Data objectData, Data initialData) {
        if (!(objectData instanceof DataObject objectDataCast)) {
            throw new IllegalArgumentException("Top-level saved data must be an object");
        }
        String objectID = objectDataCast.getID();
        if (objectID == null) {
            throw new IllegalArgumentException("Top-level object must have an ID");
        }
        String categoryID = objectDataCast.getTemplate().id();
        if (initialData != null) {
            String initialID = ((DataObject) initialData).getID();
            String newID = objectDataCast.getID();
            if (!initialID.equals(newID)) { // Edit with new ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).remove(initialID);
                browserFrame.removeGameObject(categoryID, initialID);
                data.get(categoryID).put(objectID, objectData);
                browserFrame.addGameObject(categoryID, objectID, true);
            } else { // Edit with same ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).put(objectID, objectData);
                browserFrame.updateCategory(categoryID);
            }
        } else { // New object instance
            if (!data.containsKey(categoryID)) {
                data.put(categoryID, new HashMap<>());
            }
            data.get(categoryID).put(objectID, objectData);
            browserFrame.addGameObject(categoryID, objectID, true);
        }
    }

    public void newProject() {
        // TODO - Add save confirmation if a project is open
        data.clear();
        configMenuHandler.clearConfigData();
        browserFrame.reloadBrowserData(templates, data);
        isProjectLoaded = true;
    }

    public void openProject() {
        // TODO - Add save confirmation if a project is open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(browserFrame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        data.clear();
        configMenuHandler.clearConfigData();
        try {
            DataLoader.loadFromDir(selectedDirectory, templates, data, configMenuHandler);
            browserFrame.reloadBrowserData(templates, data);
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            browserFrame.updateRecentProjects();
            isProjectLoaded = true;
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            data.clear();
            configMenuHandler.clearConfigData();
            JOptionPane.showMessageDialog(browserFrame, "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            data.clear();
            configMenuHandler.clearConfigData();
            JOptionPane.showMessageDialog(browserFrame, "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openProjectFromFile(File file) {
        // TODO - Add save confirmation if a project is open
        data.clear();
        configMenuHandler.clearConfigData();
        try {
            DataLoader.loadFromDir(file, templates, data, configMenuHandler);
            browserFrame.reloadBrowserData(templates, data);
            isProjectLoaded = true;
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            data.clear();
            configMenuHandler.clearConfigData();
            JOptionPane.showMessageDialog(browserFrame, "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            data.clear();
            configMenuHandler.clearConfigData();
            JOptionPane.showMessageDialog(browserFrame, "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(browserFrame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        try {
            DataLoader.saveToDir(selectedDirectory, templates, data, configMenuHandler);
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            browserFrame.updateRecentProjects();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(browserFrame, "Project could not be saved to the selected directory.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParserConfigurationException | TransformerException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(browserFrame, "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void newObject(String categoryID) {
        browserFrame.openEditorFrame(categoryID, null, getTemplate(categoryID), null);
    }

    public void editObject(String categoryID, String objectID) {
        browserFrame.openEditorFrame(categoryID, objectID, getTemplate(categoryID), getData(categoryID, objectID));
    }

    public void duplicateObject(String categoryID, String objectID) {
        Data objectData = getData(categoryID, objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        browserFrame.addGameObject(categoryID, newObjectID, false);
        browserFrame.setSelectedNode(categoryID, objectID);
    }

    public void deleteObject(String categoryID, String objectID) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(browserFrame, "Are you sure you want to delete " + objectID + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            data.get(categoryID).remove(objectID);
            browserFrame.closeEditorFrameIfActive(categoryID, objectID);
            browserFrame.removeGameObject(categoryID, objectID);
        }
    }

    private String generateDuplicateObjectID(String categoryID, String objectID) {
        Set<String> existingIDs = getIDsForCategory(categoryID);
        String baseCopyID = objectID + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

}