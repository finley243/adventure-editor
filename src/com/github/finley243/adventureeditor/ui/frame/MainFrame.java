package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.*;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.DeleteConfirmationResult;
import com.github.finley243.adventureeditor.ui.DeleteObjectConfirmationResult;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;
import com.github.finley243.adventureeditor.undo.ObjectChange;

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
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainFrame extends ThemedFrame implements ViewActions {

    private static final String EDITOR_NAME = "AdventureEditor";
    private static final String UNNAMED_PROJECT_NAME = "UNNAMED PROJECT";

    private PresenterActions presenter;
    private boolean hasUnsavedChanges;
    private boolean isProjectLoaded;

    private final ParameterFactory parameterFactory;
    private final TemplateRegistry templateRegistry;
    private final EditorManager editorManager;

    private EditorFrame configFrame;
    private PhraseEditorFrame phraseEditorFrame;
    private final ChildFrameHandler<String> phraseFrameHandler;
    private ScriptEditorFrame scriptEditorFrame;
    private final ChildFrameHandler<String> scriptFrameHandler;
    private ReferenceListFrame referenceListFrame;

    private final BrowserFrame browserFrame;

    private final JMenu fileOpenRecent;

    private String projectName;

    public MainFrame(ParameterFactory parameterFactory, TemplateRegistry templateRegistry) {
        super(EDITOR_NAME);
        this.parameterFactory = parameterFactory;
        this.templateRegistry = templateRegistry;
        this.editorManager = new EditorManager();
        this.phraseFrameHandler = new ChildFrameHandler<>();
        this.scriptFrameHandler = new ChildFrameHandler<>();

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
        toolsPhraseEditor.addActionListener(e -> getPresenter().onOpenPhraseMenu());
        toolsMenu.add(toolsPhraseEditor);
        JMenuItem toolsScriptEditor = new JMenuItem("Script Editor");
        toolsScriptEditor.addActionListener(e -> getPresenter().onOpenScriptMenu());
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
        windowCloseAll.addActionListener(e -> editorManager.requestCloseAllEditorFrames());
        windowMenu.add(windowCloseAll);

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new BorderLayout());
        //this.getContentPane().add(primaryPanel);
        add(primaryPanel, BorderLayout.CENTER);

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

        this.browserFrame = new BrowserFrame(this, templateRegistry);
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
            boolean shouldClose = getPresenter().onCloseProgram();
            if (shouldClose) {
                super.processWindowEvent(e);
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    @Override
    public void browserAddObject(String categoryID, String objectID) {
        browserFrame.addGameObject(categoryID, objectID);
        browserFrame.setSelectedNode(categoryID, objectID);
    }

    @Override
    public void browserRemoveObject(String categoryID, String objectID) {
        browserFrame.removeGameObject(categoryID, objectID);
    }

    @Override
    public void reregisterObject(String categoryID, String oldObjectID, String newObjectID) {
        editorManager.renameActiveTopLevelFrame(categoryID, oldObjectID, newObjectID);
    }

    @Override
    public void browserClear() {
        browserFrame.clearCategories();
    }

    @Override
    public void browserLoadObjects(Map<String, Set<String>> objects) {
        browserFrame.reloadBrowserObjects(objects);
    }

    @Override
    public void openConfigEditor(Data initialData, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate) {
        if (configFrame != null) {
            configFrame.toFront();
            configFrame.requestFocus();
        } else {
            Consumer<EditorFrame> onClose = _ -> configFrame = null;
            configFrame = new EditorFrame(this, templateRegistry.getConfigTemplate(), initialData, true, parameterFactory, getPresenter(), onInitialize, onSave, onValidate, onClose);
        }
    }

    @Override
    public void openEditorFrame(Template template, String objectID, Data initialData, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate) {
        EditorFrame activeFrame = editorManager.getActiveTopLevelFrame(template.id(), objectID);
        if (activeFrame != null) {
            activeFrame.toFront();
            activeFrame.requestFocus();
        } else {
            Consumer<EditorFrame> onClose = _ -> editorManager.removeActiveTopLevelFrame(template.id(), objectID);
            EditorFrame editorFrame = new EditorFrame(this, template, initialData, true, parameterFactory, getPresenter(), onInitialize, onSave, onValidate, onClose);
            editorManager.addActiveTopLevelFrame(template.id(), objectID, editorFrame);
        }
    }

    @Override
    public void openPhraseMenu(Map<String, String> phrases) {
        if (phraseEditorFrame != null) {
            phraseEditorFrame.toFront();
            phraseEditorFrame.requestFocus();
        } else {
            phraseEditorFrame = new PhraseEditorFrame(this, getPresenter(), () -> {
                phraseFrameHandler.closeAll();
                phraseEditorFrame = null;
            });
        }
        updatePhrases(phrases);
    }

    @Override
    public void openPhraseEditor(String phraseKey, Data content, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate) {
        boolean isOpen = phraseFrameHandler.requestFocusIfOpen(phraseKey);
        if (!isOpen) {
            Consumer<EditorFrame> onClose = phraseFrameHandler::removeChildFrame;
            EditorFrame editorFrame = new EditorFrame(phraseEditorFrame, InternalTemplates.PHRASE_TEMPLATE, content, true, parameterFactory, getPresenter(), onInitialize, onSave, onValidate, onClose);
            phraseFrameHandler.add(phraseKey, editorFrame);
        }
    }

    @Override
    public void updatePhrases(Map<String, String> phrases) {
        if (phraseEditorFrame != null) {
            phraseEditorFrame.reloadPhrases(phrases);
        }
    }

    @Override
    public void openScriptMenu(Map<String, String> scripts) {
        if (scriptEditorFrame != null) {
            scriptEditorFrame.toFront();
            scriptEditorFrame.requestFocus();
        } else {
            scriptEditorFrame = new ScriptEditorFrame(this, getPresenter(), () -> {
                scriptFrameHandler.closeAll();
                scriptEditorFrame = null;
            });
        }
        updateScripts(scripts);
    }

    @Override
    public void openScriptEditor(String name, Data content, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate) {
        boolean isOpen = scriptFrameHandler.requestFocusIfOpen(name);
        if (!isOpen) {
            Consumer<EditorFrame> onClose = scriptFrameHandler::removeChildFrame;
            EditorFrame editorFrame = new EditorFrame(scriptEditorFrame, InternalTemplates.SCRIPT_TEMPLATE, content, true, parameterFactory, getPresenter(), onInitialize, onSave, onValidate, onClose);
            editorFrame.setResizable(true);
            editorFrame.setSize(new Dimension(800, 800));
            editorFrame.setLocationRelativeTo(null);
            scriptFrameHandler.add(name, editorFrame);
        }
    }

    @Override
    public String promptScriptName() {
        return JOptionPane.showInputDialog(scriptEditorFrame, "Enter a name for the script:");
    }

    @Override
    public void updateScripts(Map<String, String> scripts) {
        if (scriptEditorFrame != null) {
            scriptEditorFrame.reloadScripts(scripts);
        }
    }

    @Override
    public void openReferenceList(Set<Reference> references) {
        if (referenceListFrame != null) {
            referenceListFrame.toFront();
            referenceListFrame.requestFocus();
        } else {
            referenceListFrame = new ReferenceListFrame(this, (category, object) -> getPresenter().onOpenReference(category, object), () -> referenceListFrame = null);
        }
        referenceListFrame.loadReferences(references);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public SaveConfirmationResult confirmProjectSave() {
        int result = JOptionPane.showConfirmDialog(this, "Save changes to the current project?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            return SaveConfirmationResult.YES;
        } else if (result == JOptionPane.NO_OPTION) {
            return SaveConfirmationResult.NO;
        } else {
            return SaveConfirmationResult.CANCEL;
        }
    }

    @Override
    public DeleteConfirmationResult confirmDeletePhrase(String deleteName) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(phraseEditorFrame, "Are you sure you want to delete " + deleteName + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            return DeleteConfirmationResult.DELETE;
        } else {
            return  DeleteConfirmationResult.CANCEL;
        }
    }

    @Override
    public DeleteConfirmationResult confirmDeleteScript(String deleteName) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(scriptEditorFrame, "Are you sure you want to delete " + deleteName + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            return DeleteConfirmationResult.DELETE;
        } else {
            return  DeleteConfirmationResult.CANCEL;
        }
    }

    @Override
    public DeleteObjectConfirmationResult confirmDeleteObject(String objectID, int referenceCount) {
        Object[] confirmOptions;
        if (referenceCount > 0) {
            confirmOptions = new Object[]{"Delete", "View References", "Cancel"};
        } else {
            confirmOptions = new Object[]{"Delete", "Cancel"};
        }
        int confirmResult = JOptionPane.showOptionDialog(browserFrame, "Are you sure you want to delete " + objectID + "?\nReferences: " + referenceCount, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            return DeleteObjectConfirmationResult.DELETE;
        } else if (confirmResult == 1 && referenceCount > 0) {
            return DeleteObjectConfirmationResult.VIEW_REFERENCES;
        } else {
            return DeleteObjectConfirmationResult.CANCEL;
        }
    }

    @Override
    public File selectSaveDirectory() {
        return selectProjectSaveDirectory();
    }

    @Override
    public void setProjectIsLoaded(boolean isProjectLoaded) {
        this.isProjectLoaded = isProjectLoaded;
        updateTitleBar();
    }

    @Override
    public void updateProjectName(String name) {
        this.projectName = name;
        updateTitleBar();
    }

    @Override
    public void setHasUnsavedProjectChanges(boolean hasUnsaved) {
        this.hasUnsavedChanges = hasUnsaved;
        updateTitleBar();
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

    @Override
    public void closeObject(String categoryID, String objectID) {
        EditorFrame frame = editorManager.getActiveTopLevelFrame(categoryID, objectID);
        if (frame != null) {
            frame.requestClose();
            editorManager.removeActiveTopLevelFrame(categoryID, objectID);
        }
    }

    @Override
    public void forceCloseConfig() {
        if (configFrame != null) {
            configFrame.requestClose();
            configFrame = null;
        }
    }

    @Override
    public void closeScript(String name) {
        EditorFrame frame = scriptFrameHandler.get(name);
        if (frame != null) {
            frame.requestClose();
            scriptFrameHandler.removeChildFrame(frame);
        }
    }

    @Override
    public void closePhrase(String key) {
        EditorFrame frame = phraseFrameHandler.get(key);
        if (frame != null) {
            frame.requestClose();
            phraseFrameHandler.removeChildFrame(frame);
        }
    }

    @Override
    public boolean hasOpenEditors() {
        if (configFrame != null) return true;
        if (scriptFrameHandler.hasAnyOpenFrame()) return true;
        if (phraseFrameHandler.hasAnyOpenFrame()) return true;
        return editorManager.hasAnyOpenFrame();
    }

    @Override
    public void closeAllEditors() {
        if (configFrame != null) {
            configFrame.requestClose();
            configFrame = null;
        }
        scriptFrameHandler.closeAll();
        if (scriptEditorFrame != null) {
            scriptEditorFrame.dispose();
            scriptEditorFrame = null;
        }
        phraseFrameHandler.closeAll();
        if (phraseEditorFrame != null) {
            phraseEditorFrame.dispose();
            phraseEditorFrame = null;
        }
        editorManager.requestCloseAllEditorFrames();
    }

    @Override
    public void updateObjectData(List<ObjectChange> changes) {
        // TODO - Implement
    }

    @Override
    public void updateUndoRedoButtons(boolean canUndo, boolean canRedo) {
        // TODO - Implement
    }

    private void attemptOpeningRecentProject(ProjectFile projectFile) {
        File file = new File(projectFile.absolutePath());
        if (!file.exists()) {
            int choice = JOptionPane.showOptionDialog(this, "The selected project file was not found. Remove it from recent projects?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Yes", "No"}, "No");
            boolean deleteMissingProject = choice == JOptionPane.YES_OPTION;
            if (deleteMissingProject) {
                getPresenter().onRemoveRecentProject(projectFile);
            }
        } else {
            getPresenter().onOpenProject(file);
        }
    }

    private void updateTitleBar() {
        if (!isProjectLoaded) {
            this.setTitle(EDITOR_NAME);
        } else if (projectName == null) {
            this.setTitle(EDITOR_NAME + " - " + (hasUnsavedChanges ? "*" : "") + UNNAMED_PROJECT_NAME);
        } else {
            this.setTitle(EDITOR_NAME + " - " + (hasUnsavedChanges ? "*" : "") + projectName);
        }
    }

}
