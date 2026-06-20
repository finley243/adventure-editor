package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.TemplateRegistry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    public void onShowReferences(String categoryID, String objectID) {

    }

    @Override
    public void onOpenPhraseEditor() {

    }

    @Override
    public void onOpenPhrase(String phraseKey) {

    }

    @Override
    public void onOpenScriptEditor() {

    }

    @Override
    public void onOpenScript(String scriptName) {

    }

    @Override
    public void onNewScript() {

    }

    @Override
    public void onDeleteScript(String scriptName) {

    }

    @Override
    public void onOpenConfigEditor() {

    }

    private Data generateDataForPhrase(String phraseKey) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put("key", new DataString(phraseKey));
        dataMap.put("text", new DataString(phraseEditorManager.getPhrase(phraseKey)));
        return new DataObject(InternalTemplates.PHRASE_TEMPLATE, dataMap);
    }

    private Data generateDataForScript(String phraseKey) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put("name", new DataString(phraseKey));
        dataMap.put("script", new DataScript(scriptEditorManager.getScript(phraseKey)));
        return new DataObject(InternalTemplates.SCRIPT_TEMPLATE, dataMap);
    }

}
