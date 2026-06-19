package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.*;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.ProjectNameChangeListener;
import com.github.finley243.adventureeditor.ui.RecentProjectListener;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
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
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainFrame extends JFrame implements ViewActions, ProjectNameChangeListener, RecentProjectListener {

    private static final String EDITOR_NAME = "AdventureEditor";

    private PresenterActions presenter;
    private boolean hasUnsavedChanges;
    private boolean isProjectLoaded;

    private final ParameterFactory parameterFactory;
    private final EditorManager editorManager;

    private PhraseEditorFrame phraseEditorFrame;
    private ScriptEditorFrame scriptEditorFrame;
    private ReferenceListFrame referenceListFrame;

    private final BrowserFrame browserFrame;

    private final JMenu fileOpenRecent;

    public MainFrame(ParameterFactory parameterFactory) {
        super(EDITOR_NAME);
        this.parameterFactory = parameterFactory;
        this.editorManager = new EditorManager();
        this.browserFrame = new BrowserFrame(this, editorManager, parameterFactory);

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileNew = new JMenuItem("New");
        fileNew.addActionListener(e -> getPresenter().onNewProject());
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> getPresenter().onOpenProject(selectProjectLoadDirectory()));
        this.fileOpenRecent = new JMenu("Open Recent");
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> getPresenter().onSaveProject());
        JMenuItem fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> getPresenter().onSaveProjectAs(selectProjectSaveDirectory()));
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                fileSave.setEnabled(hasUnsavedChanges);
                fileSaveAs.setEnabled(isProjectLoaded);
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
        toolsProjectConfig.addActionListener(e -> getPresenter().onOpenConfigEditor());
        toolsMenu.add(toolsProjectConfig);
        JMenuItem toolsPhraseEditor = new JMenuItem("Phrase Editor");
        toolsPhraseEditor.addActionListener(e -> getPresenter().onOpenPhraseEditor());
        toolsMenu.add(toolsPhraseEditor);
        JMenuItem toolsScriptEditor = new JMenuItem("Script Editor");
        toolsScriptEditor.addActionListener(e -> getPresenter().onOpenScriptEditor());
        toolsMenu.add(toolsScriptEditor);
        toolsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
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
                getPresenter().onNewProject();
            }
        };
        Action openProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPresenter().onOpenProject(selectProjectLoadDirectory());
            }
        };
        Action saveProjectAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPresenter().onSaveProject();
            }
        };
        Action saveProjectAsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPresenter().onSaveProjectAs(selectProjectSaveDirectory());
            }
        };
        Action openConfigAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPresenter().onOpenConfigEditor();
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

    public void registerPresenter(PresenterActions presenter) {
        if (this.presenter != null) throw new IllegalStateException("Presenter is already registered");
        this.presenter = presenter;
        browserFrame.registerPresenter(presenter);
    }

    private PresenterActions getPresenter() {
        if (presenter == null) throw new IllegalStateException("Presenter has not been registered");
        return presenter;
    }

    public void setProjectName(String name) {
        if (name == null) {
            this.setTitle(EDITOR_NAME);
        } else {
            this.setTitle(EDITOR_NAME + " - " + (hasUnsavedChanges ? "*" : "") + name);
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
            if (isProjectLoaded && hasUnsavedChanges) {
                SaveConfirmationResult result = projectSaveConfirmation();
                if (result == SaveConfirmationResult.YES) {
                    getPresenter().onSaveProject();
                    super.processWindowEvent(e);
                } else if (result == SaveConfirmationResult.NO) {
                    super.processWindowEvent(e);
                }
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
            recentProjectItem.addActionListener(e -> attemptOpeningRecentProject(recentProject));
            fileOpenRecent.add(recentProjectItem);
        }
        JSeparator separator = new JSeparator();
        fileOpenRecent.add(separator);
        JMenuItem clearRecentProjects = new JMenuItem("Clear Recent Projects");
        clearRecentProjects.addActionListener(e -> getPresenter().onClearRecentProjects());
        fileOpenRecent.add(clearRecentProjects);
    }

    @Override
    public void openEditorFrame(String editorID, Template template, Data data, BiConsumer<Data, Data> onSave, Function<Data, DataSaveTarget.ErrorData> onValidate) {

    }

    @Override
    public void openPhraseEditor(Map<String, String> phrases, Consumer<Map<String, String>> onSave) {

    }

    @Override
    public void openScriptEditor(Map<String, String> scripts, Consumer<Map<String, String>> onSave) {

    }

    @Override
    public void showError(String message) {
        this.showErrorDialog(message);
    }

    @Override
    public SaveConfirmationResult confirmProjectSave() {
        return this.projectSaveConfirmation();
    }

    @Override
    public File selectSaveDirectory() {
        return selectProjectSaveDirectory();
    }

    @Override
    public void setProjectIsLoaded(boolean isProjectLoaded) {
        this.isProjectLoaded = isProjectLoaded;
    }

    @Override
    public void updateProjectName(String name) {
        setProjectName(name);
    }

    @Override
    public void setUnsavedChanges(boolean hasUnsaved) {
        this.hasUnsavedChanges = hasUnsaved;
    }

    @Override
    public void updateRecentProjects(List<ProjectFile> recentProjects) {
        fileOpenRecent.setEnabled(!recentProjects.isEmpty());
        fileOpenRecent.removeAll();
        for (ProjectFile recentProject : recentProjects) {
            JMenuItem recentProjectItem = new JMenuItem(recentProject.name());
            recentProjectItem.addActionListener(e -> attemptOpeningRecentProject(recentProject));
            fileOpenRecent.add(recentProjectItem);
        }
        JSeparator separator = new JSeparator();
        fileOpenRecent.add(separator);
        JMenuItem clearRecentProjects = new JMenuItem("Clear Recent Projects");
        clearRecentProjects.addActionListener(e -> getPresenter().onClearRecentProjects());
        fileOpenRecent.add(clearRecentProjects);
    }

    private void attemptOpeningRecentProject(ProjectFile projectFile) {
        File file = new File(projectFile.absolutePath());
        if (!file.exists()) {
            boolean deleteMissingProject = recentProjectDeleteConfirmation();
            if (deleteMissingProject) {
                getPresenter().onRemoveRecentProject(projectFile);
            }
        }
    }

}
