package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.*;
import com.github.finley243.adventureeditor.ui.ProjectNameChangeListener;
import com.github.finley243.adventureeditor.ui.RecentProjectListener;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public class MainFrame extends JFrame implements ProjectNameChangeListener, RecentProjectListener {

    private static final String EDITOR_NAME = "AdventureEditor";

    private final ParameterFactory parameterFactory;
    private final DataManager dataManager;
    private final ProjectManager projectManager;
    private final ConfigMenuManager configMenuManager;
    private final PhraseEditorManager phraseEditorManager;
    private final ScriptEditorManager scriptEditorManager;
    private final EditorManager editorManager;

    private final JMenu fileOpenRecent;

    public MainFrame(ParameterFactory parameterFactory, DataManager dataManager, ProjectManager projectManager, ConfigMenuManager configMenuManager, PhraseEditorManager phraseEditorManager, ScriptEditorManager scriptEditorManager, EditorManager editorManager) {
        super(EDITOR_NAME);
        this.parameterFactory = parameterFactory;
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
        fileNew.addActionListener(e -> projectManager.newProject(this, parameterFactory, this));
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> projectManager.openProjectFromMenu(this));
        this.fileOpenRecent = new JMenu("Open Recent");
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> projectManager.saveProjectToCurrentPath(this));
        JMenuItem fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> projectManager.saveProjectToMenu(this));
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
        toolsProjectConfig.addActionListener(e -> configMenuManager.openConfigMenu(this, parameterFactory));
        toolsMenu.add(toolsProjectConfig);
        JMenuItem toolsPhraseEditor = new JMenuItem("Phrase Editor");
        toolsPhraseEditor.addActionListener(e -> phraseEditorManager.openPhraseEditor(this, parameterFactory));
        toolsMenu.add(toolsPhraseEditor);
        JMenuItem toolsScriptEditor = new JMenuItem("Script Editor");
        toolsScriptEditor.addActionListener(e -> scriptEditorManager.openScriptEditor(this, parameterFactory));
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
                projectManager.newProject(MainFrame.this, parameterFactory, MainFrame.this);
            }
        };
        Action openProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.openProjectFromMenu(MainFrame.this);
            }
        };
        Action saveProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.saveProjectToCurrentPath(MainFrame.this);
            }
        };
        Action saveProjectAsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectManager.saveProjectToMenu(MainFrame.this);
            }
        };
        Action openConfigAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configMenuManager.openConfigMenu(MainFrame.this, parameterFactory);
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

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean recentProjectDeleteConfirmation() {
        int choice = JOptionPane.showOptionDialog(this, "The selected project file was not found. Remove it from recent projects?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Yes", "No"}, "No");
        return choice == JOptionPane.YES_OPTION;
    }

    public SaveConfirmationResult projectSaveConfirmation() {
        int result = JOptionPane.showConfirmDialog(this, "Save changes to the current project?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            return SaveConfirmationResult.YES;
        } else if (result == JOptionPane.NO_OPTION) {
            return SaveConfirmationResult.NO;
        } else {
            return SaveConfirmationResult.CANCEL;
        }
    }

    public File selectProjectSaveDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public File selectProjectLoadDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            boolean shouldClose = projectManager.saveConfirmationIfHasUnsavedData(this);
            if (shouldClose) {
                super.processWindowEvent(e);
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    @Override
    public void onProjectNameChange(String name) {
        setProjectName(name);
    }

    @Override
    public void onUpdateRecentProjects(List<ProjectFile> recentProjects) {
        fileOpenRecent.setEnabled(!recentProjects.isEmpty());
        fileOpenRecent.removeAll();
        for (ProjectFile recentProject : recentProjects) {
            JMenuItem recentProjectItem = new JMenuItem(recentProject.name());
            recentProjectItem.addActionListener(e -> projectManager.openRecentProject(recentProject, this));
            fileOpenRecent.add(recentProjectItem);
        }
        JSeparator separator = new JSeparator();
        fileOpenRecent.add(separator);
        JMenuItem clearRecentProjects = new JMenuItem("Clear Recent Projects");
        clearRecentProjects.addActionListener(e -> projectManager.clearRecentProjects());
        fileOpenRecent.add(clearRecentProjects);
    }

}
