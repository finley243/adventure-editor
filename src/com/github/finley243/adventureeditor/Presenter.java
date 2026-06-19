package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.TemplateRegistry;

import java.io.File;

public class Presenter implements PresenterActions {

    private final DataManager dataManager;
    private final ProjectManager projectManager;
    private final ConfigMenuManager configMenuManager;
    private final PhraseEditorManager phraseEditorManager;
    private final ScriptEditorManager scriptEditorManager;
    private final ReferenceListManager referenceListManager;
    private final TemplateRegistry templateRegistry;
    private final DataLoader dataLoader;
    private final ViewActions view;

    public Presenter(DataManager dataManager, ProjectManager projectManager, ConfigMenuManager configMenuManager, PhraseEditorManager phraseEditorManager, ScriptEditorManager scriptEditorManager, ReferenceListManager referenceListManager, TemplateRegistry templateRegistry, DataLoader dataLoader, ViewActions view) {
        this.dataManager = dataManager;
        this.projectManager = projectManager;
        this.configMenuManager = configMenuManager;
        this.phraseEditorManager = phraseEditorManager;
        this.scriptEditorManager = scriptEditorManager;
        this.referenceListManager = referenceListManager;
        this.templateRegistry = templateRegistry;
        this.dataLoader = dataLoader;
        this.view = view;
    }

    @Override
    public void onNewProject() {

    }

    @Override
    public void onOpenProject(File file) {

    }

    @Override
    public void onSaveProject() {

    }

    @Override
    public void onSaveProjectAs(File file) {

    }

    @Override
    public void onRemoveRecentProject(ProjectFile projectFile) {

    }

    @Override
    public void onClearRecentProjects() {

    }

    @Override
    public void onCreateObject(String categoryID) {

    }

    @Override
    public void onEditObject(String categoryID, String objectID) {

    }

    @Override
    public void onDeleteObject(String categoryID, String objectID) {

    }

    @Override
    public void onDuplicateObject(String categoryID, String objectID) {

    }

    @Override
    public void onOpenPhraseEditor() {

    }

    @Override
    public void onOpenScriptEditor() {

    }

    @Override
    public void onOpenConfigEditor() {

    }

}
