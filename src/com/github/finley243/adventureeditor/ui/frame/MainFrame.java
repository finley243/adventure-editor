package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.ProjectData;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;

public class MainFrame extends JFrame implements DataSaveTarget {

    private final Main main;
    private final JMenu fileOpenRecent;

    public MainFrame(Main main) {
        super("AdventureEditor");
        this.main = main;

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
        JMenuItem toolsProjectConfig = new JMenuItem("Project Configuration");
        toolsProjectConfig.addActionListener(e -> main.getConfigMenuManager().openConfigMenu());
        toolsMenu.add(toolsProjectConfig);
        JMenuItem toolsPhraseEditor = new JMenuItem("Phrase Editor");
        toolsPhraseEditor.addActionListener(e -> main.getPhraseEditorManager().openPhraseEditor());
        toolsMenu.add(toolsPhraseEditor);
        JMenuItem toolsScriptEditor = new JMenuItem("Script Editor");
        toolsScriptEditor.addActionListener(e -> main.getScriptEditorManager().openScriptEditor());
        toolsMenu.add(toolsScriptEditor);
        toolsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                boolean isProjectLoaded = main.getProjectManager().isProjectLoaded();
                toolsProjectConfig.setEnabled(isProjectLoaded);
                toolsPhraseEditor.setEnabled(isProjectLoaded);
                toolsScriptEditor.setEnabled(isProjectLoaded);
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        JMenu windowMenu = new JMenu("Window");
        menuBar.add(windowMenu);
        JMenuItem windowCloseAll = new JMenuItem("Close All Objects");
        windowCloseAll.addActionListener(e -> main.getEditorManager().closeAllActiveEditorFrames());
        windowMenu.add(windowCloseAll);

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new BorderLayout());
        this.getContentPane().add(primaryPanel);

        Action newProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getProjectManager().newProject();
            }
        };
        Action openProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getProjectManager().openProjectFromMenu();
            }
        };
        Action saveProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getProjectManager().saveProjectToMenu();
            }
        };
        Action saveProjectAsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getProjectManager().saveProjectToCurrentPath();
            }
        };
        Action openConfigAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getConfigMenuManager().openConfigMenu();
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("newProject", newProjectAction);
        actionMap.put("openProject", openProjectAction);
        actionMap.put("saveProject", saveProjectAction);
        actionMap.put("saveProjectAs", saveProjectAsAction);
        actionMap.put("openConfig", openConfigAction);

        InputMap inputMapWindow = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "newProject");
        inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "openProject");
        inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveProject");
        inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "saveProjectAs");
        inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "openConfig");

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
        JSeparator separator = new JSeparator();
        fileOpenRecent.add(separator);
        JMenuItem clearRecentProjects = new JMenuItem("Clear Recent Projects");
        clearRecentProjects.addActionListener(e -> main.getProjectManager().clearRecentProjects());
        fileOpenRecent.add(clearRecentProjects);
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
    public void saveObjectData(String editorID, Data data, Data initialData) {
        main.getDataManager().saveObjectData(data, initialData);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        main.getEditorManager().closeEditorFrameIfActive(frame.getTemplate().id(), frame.getObjectID());
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
