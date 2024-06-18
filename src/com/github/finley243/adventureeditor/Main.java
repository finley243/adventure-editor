package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.ProjectData;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main implements DataSaveTarget {

    private final Map<String, Template> templates;
    private final Map<String, Set<String>> enumTypes;
    private final List<ProjectData> recentProjects;
    private final Map<String, Map<String, Data>> data;

    private final BrowserTree browserTree;
    private final JMenu fileOpenRecent;

    private final Map<String, Map<String, EditorFrame>> topLevelEditorWindows;

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
        DataLoader.loadTemplates(templates, enumTypes);
        DataLoader.loadRecentProjects(recentProjects);
        this.data = new HashMap<>();
        this.browserTree = new BrowserTree(this);
        this.fileOpenRecent = new JMenu("Open Recent");
        this.topLevelEditorWindows = new HashMap<>();
        isProjectLoaded = false;
        EventQueue.invokeLater(this::run);
    }

    public void run() {
        JFrame frame = new JFrame("AdventureEditor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileNew = new JMenuItem("New");
        fileNew.addActionListener(e -> newProject());
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> openProject());
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> saveProject());
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                fileSave.setEnabled(isProjectLoaded);
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileMenu.add(fileOpenRecent);
        fileMenu.add(fileSave);
        updateRecentProjects();

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());
        //loadBrowserCategories();

        browserTree.setPreferredSize(new Dimension(400, 400));
        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setPreferredSize(new Dimension(400, 800));
        browserPanel.add(browserScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(browserPanel);

        frame.setVisible(true);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void updateRecentProjects() {
        fileOpenRecent.setEnabled(!recentProjects.isEmpty());
        fileOpenRecent.removeAll();
        for (ProjectData recentProject : recentProjects) {
            JMenuItem recentProjectItem = new JMenuItem(recentProject.name());
            recentProjectItem.addActionListener(e -> {
                File recentProjectFile = new File(recentProject.absolutePath());
                if (recentProjectFile.exists()) {
                    recentProjects.remove(recentProject);
                    recentProjects.addFirst(recentProject);
                    openProjectFromFile(recentProjectFile);
                } else {
                    JOptionPane.showMessageDialog(browserTree, "The selected project file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            fileOpenRecent.add(recentProjectItem);
        }
        DataLoader.saveRecentProjects(recentProjects);
    }

    public Template getTemplate(String categoryID) {
        return templates.get(categoryID);
    }

    public Set<String> getEnumValues(String enumID) {
        return enumTypes.get(enumID);
    }

    public Set<String> getIDsForCategory(String categoryID) {
        if (!data.containsKey(categoryID)) {
            return null;
        }
        return data.get(categoryID).keySet();
    }

    public Data getData(String categoryID, String object) {
        if (data.get(categoryID) == null) {
            return null;
        }
        return data.get(categoryID).get(object);
    }

    @Override
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
                data.get(categoryID).remove(initialID);
                browserTree.removeGameObject(categoryID, initialID);
                data.get(categoryID).put(objectID, objectData);
                browserTree.addGameObject(categoryID, objectID, true);
            } else { // Edit with same ID
                data.get(categoryID).put(objectID, objectData);
                browserTree.updateCategory(categoryID);
            }
        } else { // New object instance
            data.get(categoryID).put(objectID, objectData);
            browserTree.addGameObject(categoryID, objectID, true);
        }
    }

    private void loadBrowserData() {
        browserTree.clearData();
        browserTree.expandRow(0);
        for (String category : templates.keySet()) {
            if (templates.get(category).topLevel()) {
                browserTree.addCategory(category, templates.get(category).name());
            }
        }
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                browserTree.addGameObject(category, object, false);
            }
        }
    }

    public void newProject() {
        // TODO - Add save confirmation if a project is open
        data.clear();
        loadBrowserData();
        isProjectLoaded = true;
    }

    public void openProject() {
        // TODO - Add save confirmation if a project is open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(browserTree);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        data.clear();
        try {
            DataLoader.loadFromDir(selectedDirectory, templates, data);
            loadBrowserData();
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            updateRecentProjects();
            isProjectLoaded = true;
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            data.clear();
            JOptionPane.showMessageDialog(browserTree, "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            data.clear();
            JOptionPane.showMessageDialog(browserTree, "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openProjectFromFile(File file) {
        // TODO - Add save confirmation if a project is open
        data.clear();
        try {
            DataLoader.loadFromDir(file, templates, data);
            loadBrowserData();
            isProjectLoaded = true;
        } catch (ParserConfigurationException | SAXException e) {
            //throw new RuntimeException(e);
            data.clear();
            JOptionPane.showMessageDialog(browserTree, "The selected project has data that is improperly formed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            data.clear();
            JOptionPane.showMessageDialog(browserTree, "The selected project directory cannot be read.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(browserTree);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDirectory = fileChooser.getSelectedFile();
        try {
            DataLoader.saveToDir(selectedDirectory, templates, data);
            ProjectData project = new ProjectData(selectedDirectory.getName(), selectedDirectory.getAbsolutePath());
            recentProjects.remove(project);
            recentProjects.addFirst(project);
            updateRecentProjects();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(browserTree, "Project could not be saved to the selected directory.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParserConfigurationException | TransformerException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(browserTree, "Save system encountered an error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void newObject(String categoryID) {
        openEditorFrame(categoryID, null);
    }

    public void editObject(String categoryID, String objectID) {
        openEditorFrame(categoryID, objectID);
    }

    public void duplicateObject(String categoryID, String objectID) {
        Data objectData = data.get(categoryID).get(objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        browserTree.addGameObject(categoryID, newObjectID, false);
        browserTree.setSelectedNode(categoryID, objectID);
    }

    public void deleteObject(String categoryID, String objectID) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(browserTree, "Are you sure you want to delete " + objectID + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            data.get(categoryID).remove(objectID);
            EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
            if (activeFrame != null) {
                activeFrame.dispose();
                removeActiveTopLevelFrame(categoryID, objectID);
            }
            browserTree.removeGameObject(categoryID, objectID);
        }
    }

    public void openEditorFrame(String categoryID, String objectID) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.toFront();
            activeFrame.requestFocus();
        } else {
            Template template = templates.get(categoryID);
            Data objectData = data.get(categoryID).get(objectID);
            EditorFrame editorFrame = new EditorFrame(this, template, objectData, this);
            addActiveTopLevelFrame(categoryID, objectID, editorFrame);
        }
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        removeActiveTopLevelFrame(frame.getTemplate().id(), frame.getObjectID());
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

    private EditorFrame getActiveTopLevelFrame(String categoryID, String objectID) {
        if (categoryID == null | objectID == null) {
            return null;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            return null;
        }
        return topLevelEditorWindows.get(categoryID).get(objectID);
    }

    private void addActiveTopLevelFrame(String categoryID, String objectID, EditorFrame frame) {
        if (objectID == null) {
            return;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            topLevelEditorWindows.put(categoryID, new HashMap<>());
        }
        topLevelEditorWindows.get(categoryID).put(objectID, frame);
    }

    private void removeActiveTopLevelFrame(String categoryID, String objectID) {
        if (objectID == null) {
            return;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            return;
        }
        topLevelEditorWindows.get(categoryID).remove(objectID);
        if (topLevelEditorWindows.get(categoryID).isEmpty()) {
            topLevelEditorWindows.remove(categoryID);
        }
    }

}