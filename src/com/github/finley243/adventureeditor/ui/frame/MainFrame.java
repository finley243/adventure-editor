package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.*;
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

public class MainFrame extends JFrame {

    private static final String EDITOR_NAME = "AdventureEditor";

    private final DataManager dataManager;
    private final ProjectManager projectManager;
    private final ConfigMenuManager configMenuManager;
    private final PhraseEditorManager phraseEditorManager;
    private final ScriptEditorManager scriptEditorManager;
    private final EditorManager editorManager;

    private final JMenu fileOpenRecent;

    public MainFrame(DataManager dataManager, ProjectManager projectManager, ConfigMenuManager configMenuManager, PhraseEditorManager phraseEditorManager, ScriptEditorManager scriptEditorManager, EditorManager editorManager) {
        super(EDITOR_NAME);
        this.dataManager = dataManager;
        this.projectManager = projectManager;
        this.configMenuManager = configMenuManager;
        this.phraseEditorManager = phraseEditorManager;
        this.scriptEditorManager = scriptEditorManager;
        this.editorManager = editorManager;

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileNew = new JMenuItem("New");
        fileNew.addActionListener(e -> projectManager.newProject());
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> projectManager.openProjectFromMenu());
        this.fileOpenRecent = new JMenu("Open Recent");
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> projectManager.saveProjectToCurrentPath());
        JMenuItem fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> projectManager.saveProjectToMenu());
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                fileSave.setEnabled(projectManager.hasUnsavedChanges());
                fileSaveAs.setEnabled(projectManager.isProjectLoaded());
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
        toolsProjectConfig.addActionListener(e -> configMenuManager.openConfigMenu());
        toolsMenu.add(toolsProjectConfig);
        JMenuItem toolsPhraseEditor = new JMenuItem("Phrase Editor");
        toolsPhraseEditor.addActionListener(e -> phraseEditorManager.openPhraseEditor());
        toolsMenu.add(toolsPhraseEditor);
        JMenuItem toolsScriptEditor = new JMenuItem("Script Editor");
        toolsScriptEditor.addActionListener(e -> scriptEditorManager.openScriptEditor());
        toolsMenu.add(toolsScriptEditor);
        toolsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                boolean isProjectLoaded = projectManager.isProjectLoaded();
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
        windowCloseAll.addActionListener(e -> editorManager.closeAllActiveEditorFrames());
        windowMenu.add(windowCloseAll);

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new BorderLayout());
        this.getContentPane().add(primaryPanel);

        Action newProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.newProject();
            }
        };
        Action openProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.openProjectFromMenu();
            }
        };
        Action saveProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.saveProjectToCurrentPath();
            }
        };
        Action saveProjectAsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.saveProjectToMenu();
            }
        };
        Action openConfigAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configMenuManager.openConfigMenu();
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
        List<ProjectFile> recentProjects = projectManager.getRecentProjects();
        fileOpenRecent.setEnabled(!recentProjects.isEmpty());
        fileOpenRecent.removeAll();
        for (ProjectFile recentProject : recentProjects) {
            JMenuItem recentProjectItem = new JMenuItem(recentProject.name());
            recentProjectItem.addActionListener(e -> projectManager.openRecentProject(recentProject));
            fileOpenRecent.add(recentProjectItem);
        }
        JSeparator separator = new JSeparator();
        fileOpenRecent.add(separator);
        JMenuItem clearRecentProjects = new JMenuItem("Clear Recent Projects");
        clearRecentProjects.addActionListener(e -> projectManager.clearRecentProjects());
        fileOpenRecent.add(clearRecentProjects);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            boolean shouldClose = projectManager.saveConfirmationIfHasUnsavedData();
            if (shouldClose) {
                super.processWindowEvent(e);
            }
        } else {
            super.processWindowEvent(e);
        }
    }

}
