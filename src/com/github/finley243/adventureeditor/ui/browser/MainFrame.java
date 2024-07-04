package com.github.finley243.adventureeditor.ui.browser;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.ProjectData;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainFrame extends JFrame implements DataSaveTarget {

    private final Main main;
    private final JMenu fileOpenRecent;

    private final Map<String, Map<String, EditorFrame>> topLevelEditorWindows;

    public MainFrame(Main main) {
        super("AdventureEditor");
        this.main = main;
        this.topLevelEditorWindows = new HashMap<>();

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new BorderLayout());
        this.getContentPane().add(primaryPanel);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

    public void openEditorFrame(String categoryID, String objectID, Template template, Data objectData, DataSaveTarget saveTargetOverride) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.toFront();
            activeFrame.requestFocus();
        } else {
            EditorFrame editorFrame = new EditorFrame(main, this, template, objectData, saveTargetOverride != null ? saveTargetOverride : this, true);
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

    public void closeAllActiveEditorFrames() {
        for (String categoryID : topLevelEditorWindows.keySet()) {
            for (String objectID : topLevelEditorWindows.get(categoryID).keySet()) {
                // TODO - Check if user wants to save changes in each window if applicable
                topLevelEditorWindows.get(categoryID).get(objectID).requestClose(false, false);
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
        Set<String> objectIDsInCategory = main.getDataManager().getIDsForCategory(categoryID);
        if (isNewInstance) {
            if (objectIDsInCategory != null && objectIDsInCategory.contains(currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        } else {
            String initialID = ((DataObject) initialData).getID();
            if (!initialID.equals(currentID) && objectIDsInCategory != null && objectIDsInCategory.contains(currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        }
        return new ErrorData(false, null);
    }

}
