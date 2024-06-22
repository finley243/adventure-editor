package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.ProjectData;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserFrame extends JFrame implements DataSaveTarget {

    private final Main main;
    private final BrowserTree browserTree;
    private final JMenu fileOpenRecent;

    private final Map<String, Map<String, EditorFrame>> topLevelEditorWindows;

    public BrowserFrame(Main main) {
        super("AdventureEditor");
        this.main = main;
        this.topLevelEditorWindows = new HashMap<>();

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.browserTree = new BrowserTree(main);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileNew = new JMenuItem("New");
        fileNew.addActionListener(e -> main.getProjectManager().newProject());
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> main.getProjectManager().openProjectFromMenu());
        this.fileOpenRecent = new JMenu("Open Recent");
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> main.getProjectManager().saveProjectToCurrentPath());
        JMenuItem fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> main.getProjectManager().saveProjectToMenu());
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                fileSave.setEnabled(main.getProjectManager().hasUnsavedChanges());
                fileSaveAs.setEnabled(main.getProjectManager().isProjectLoaded());
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileMenu.add(fileOpenRecent);
        fileMenu.addSeparator();
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveAs);

        JMenu toolsMenu = new JMenu("Tools");
        menuBar.add(toolsMenu);
        JMenuItem toolsPhraseEditor = new JMenuItem("Phrase Editor");
        toolsPhraseEditor.addActionListener(e -> main.getPhraseEditorManager().openPhraseEditor());
        toolsMenu.add(toolsPhraseEditor);
        toolsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                toolsPhraseEditor.setEnabled(main.getProjectManager().isProjectLoaded());
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        JMenu settingsMenu = new JMenu("Settings");
        menuBar.add(settingsMenu);
        JMenuItem settingsProjectConfig = new JMenuItem("Project Configuration");
        settingsProjectConfig.addActionListener(e -> main.getConfigMenuManager().openConfigMenu());
        settingsMenu.add(settingsProjectConfig);
        settingsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                settingsProjectConfig.setEnabled(main.getProjectManager().isProjectLoaded());
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        //updateRecentProjects();

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());

        browserTree.setPreferredSize(new Dimension(400, 400));
        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setPreferredSize(new Dimension(400, 800));
        browserPanel.add(browserScrollPane, BorderLayout.CENTER);
        this.getContentPane().add(browserPanel);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void setProjectName(String name) {
        if (name == null) {
            this.setTitle("AdventureEditor");
        } else {
            this.setTitle("AdventureEditor - " + name);
        }
    }

    public void updateRecentProjects() {
        List<ProjectData> recentProjects = main.getProjectManager().getRecentProjects();
        fileOpenRecent.setEnabled(!recentProjects.isEmpty());
        fileOpenRecent.removeAll();
        for (ProjectData recentProject : recentProjects) {
            JMenuItem recentProjectItem = new JMenuItem(recentProject.name());
            recentProjectItem.addActionListener(e -> main.getProjectManager().openRecentProject(recentProject));
            fileOpenRecent.add(recentProjectItem);
        }
    }

    public void addGameObject(String categoryID, String newObjectID, boolean selectedAfterLoading) {
        browserTree.addGameObject(categoryID, newObjectID, selectedAfterLoading);
    }

    public void removeGameObject(String categoryID, String objectID) {
        browserTree.removeGameObject(categoryID, objectID);
    }

    public void setSelectedNode(String categoryID, String objectID) {
        browserTree.setSelectedNode(categoryID, objectID);
    }

    public void updateCategory(String categoryID) {
        browserTree.updateCategory(categoryID);
    }

    public void reloadBrowserData(Map<String, Template> templates, Map<String, Map<String, Data>> data) {
        closeAllActiveEditorFrames();
        browserTree.clearData();
        browserTree.expandRow(0);
        for (String category : templates.keySet()) {
            if (templates.get(category).topLevel()) {
                browserTree.addCategory(category, templates.get(category).name());
            }
        }
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                this.addGameObject(category, object, false);
            }
        }
    }

    public void openEditorFrame(String categoryID, String objectID, Template template, Data objectData) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.toFront();
            activeFrame.requestFocus();
        } else {
            EditorFrame editorFrame = new EditorFrame(main, template, objectData, this);
            addActiveTopLevelFrame(categoryID, objectID, editorFrame);
        }
    }

    public void closeEditorFrameIfActive(String categoryID, String objectID) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.dispose();
            removeActiveTopLevelFrame(categoryID, objectID);
        }
    }

    private void closeAllActiveEditorFrames() {
        for (String categoryID : topLevelEditorWindows.keySet()) {
            for (String objectID : topLevelEditorWindows.get(categoryID).keySet()) {
                // TODO - Check if user wants to save changes in each window if applicable
                topLevelEditorWindows.get(categoryID).get(objectID).requestClose(false, false, false);
            }
        }
        topLevelEditorWindows.clear();
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

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            boolean shouldClose = main.getProjectManager().saveConfirmationIfHasUnsavedData();
            if (shouldClose) {
                super.processWindowEvent(e);
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    @Override
    public void saveObjectData(Data data, Data initialData) {
        main.getDataManager().saveObjectData(data, initialData);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        this.closeEditorFrameIfActive(frame.getTemplate().id(), frame.getObjectID());
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        boolean isNewInstance = initialData == null;
        String categoryID = ((DataObject) currentData).getTemplate().id();
        String currentID = ((DataObject) currentData).getID();
        if (currentID == null || currentID.trim().isEmpty()) {
            return new ErrorData(true, "ID cannot be empty.");
        }
        if (isNewInstance) {
            if (main.getDataManager().getIDsForCategory(categoryID).contains(currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        } else {
            String initialID = ((DataObject) initialData).getID();
            if (!initialID.equals(currentID) && main.getDataManager().getIDsForCategory(categoryID).contains(currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        }
        return new ErrorData(false, null);
    }

}
