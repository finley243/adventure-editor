package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.DeleteConfirmationResult;
import com.github.finley243.adventureeditor.ui.DeleteObjectConfirmationResult;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Presenter implements PresenterActions {

    private static final String CONFIG_OBJECT_NAME = "config";
    private static final String PARAMETER_PHRASE_KEY = "key";
    private static final String PARAMETER_PHRASE_TEXT = "text";
    private static final String PARAMETER_SCRIPT_NAME = "name";
    private static final String PARAMETER_SCRIPT_BODY = "script";

    private final DataManager dataManager;
    private final ProjectManager projectManager;
    private final ConfigMenuManager configMenuManager;
    private final PhraseEditorManager phraseEditorManager;
    private final ScriptEditorManager scriptEditorManager;
    private final TemplateRegistry templateRegistry;
    private final DataLoader dataLoader;
    private final ViewActions view;

    public Presenter(DataManager dataManager, ProjectManager projectManager, ConfigMenuManager configMenuManager, PhraseEditorManager phraseEditorManager, ScriptEditorManager scriptEditorManager, TemplateRegistry templateRegistry, DataLoader dataLoader, ViewActions view) {
        this.dataManager = dataManager;
        this.projectManager = projectManager;
        this.configMenuManager = configMenuManager;
        this.phraseEditorManager = phraseEditorManager;
        this.scriptEditorManager = scriptEditorManager;
        this.templateRegistry = templateRegistry;
        this.dataLoader = dataLoader;
        this.view = view;
    }

    public void start() {
        projectManager.loadRecentProjects(dataLoader.loadRecentProjects());
        view.updateRecentProjects(projectManager.getRecentProjects());
        dataManager.setSavedChanges();
        configMenuManager.setSavedChanges();
        phraseEditorManager.setSavedChanges();
        scriptEditorManager.setSavedChanges();
    }

    @Override
    public void onNewProject() {
        SaveConfirmationResult result = closeProjectWithSaveConfirmation();
        if (result == SaveConfirmationResult.CANCEL) {
            return;
        } else if (result == SaveConfirmationResult.YES) {
            onSaveProject();
        }

        dataManager.unloadData();
        configMenuManager.unloadConfigData();
        phraseEditorManager.unloadPhrases();
        scriptEditorManager.unloadScripts();
        view.browserLoadObjects(dataManager.getAllObjectIDs());

        projectManager.setLoadedProjectPath(null);
        projectManager.setProjectLoaded(true);
        view.setProjectIsLoaded(true);
        updateProjectChanges();
        onOpenConfigEditor();
    }

    @Override
    public void onOpenProject(File file) {
        SaveConfirmationResult result = closeProjectWithSaveConfirmation();
        if (result == SaveConfirmationResult.CANCEL) {
            return;
        } else if (result == SaveConfirmationResult.YES) {
            onSaveProject();
        }

        String projectPath = file.getAbsolutePath();
        ProjectLoadData projectData = dataLoader.loadFromDir(new File(projectPath), templateRegistry);
        dataManager.loadData(projectData.gameData());
        configMenuManager.loadConfigData(projectData.configData());
        phraseEditorManager.loadPhrases(projectData.phrases());
        scriptEditorManager.loadScripts(projectData.scripts());
        view.browserLoadObjects(dataManager.getAllObjectIDs());

        projectManager.setProjectLoaded(true);
        view.setProjectIsLoaded(true);
        projectManager.setLoadedProjectPath(projectPath);
        projectManager.addRecentProject(new ProjectFile(file.getName(), file.getAbsolutePath()));
        dataLoader.saveRecentProjects(projectManager.getRecentProjects());
        view.updateProjectName(configMenuManager.getProjectName());
        updateProjectChanges();
    }

    @Override
    public void onSaveProject() {
        File file;
        if (projectManager.getLoadedProjectPath() != null) {
            file = new File(projectManager.getLoadedProjectPath());
        } else {
            file = view.selectSaveDirectory();
            if (file == null) return;
            projectManager.setLoadedProjectPath(file.getAbsolutePath());
            projectManager.addRecentProject(new ProjectFile(file.getName(), file.getAbsolutePath()));
            dataLoader.saveRecentProjects(projectManager.getRecentProjects());
        }
        dataLoader.saveToDir(file, templateRegistry, dataManager.getAllData(), configMenuManager.getConfigData(), scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
        setProjectChangesSaved();
    }

    @Override
    public void onSaveProjectAs(File file) {
        if (file == null) return;
        dataLoader.saveToDir(file, templateRegistry, dataManager.getAllData(), configMenuManager.getConfigData(), scriptEditorManager.getScripts(), phraseEditorManager.getPhrases());
        projectManager.setLoadedProjectPath(file.getAbsolutePath());
        projectManager.addRecentProject(new ProjectFile(file.getName(), file.getAbsolutePath()));
        dataLoader.saveRecentProjects(projectManager.getRecentProjects());
        setProjectChangesSaved();
    }

    @Override
    public void onRemoveRecentProject(ProjectFile projectFile) {
        projectManager.removeRecentProject(projectFile);
        dataLoader.saveRecentProjects(projectManager.getRecentProjects());
        view.updateRecentProjects(projectManager.getRecentProjects());
    }

    @Override
    public void onClearRecentProjects() {
        projectManager.clearRecentProjects();
        dataLoader.saveRecentProjects(List.of());
        view.updateRecentProjects(projectManager.getRecentProjects());
    }

    @Override
    public void onCreateObject(String categoryID) {
        Template template = templateRegistry.getTemplate(categoryID);
        String defaultID = generateDefaultID(categoryID, template);
        Data initialData = new DataObject(template, new HashMap<>());
        dataManager.setData(categoryID, defaultID, initialData);
        if (template.topLevel()) {
            view.browserAddObject(categoryID, defaultID);
        }
        AtomicReference<String> savedKey = new AtomicReference<>(defaultID);
        view.openEditorFrame(template, defaultID, initialData, data -> {
            savedKey.set(saveObjectData(data, savedKey.get()));
            updateProjectChanges();
            }, data -> validateObject(data, savedKey.get()));
    }

    @Override
    public void onCreateObject(String categoryID, Consumer<Data> onSave) {
        Template template = templateRegistry.getTemplate(categoryID);
        String defaultID = generateDefaultID(categoryID, template);
        Data initialData = new DataObject(template, new HashMap<>());
        dataManager.setData(categoryID, defaultID, initialData);
        if (template.topLevel()) {
            view.browserAddObject(categoryID, defaultID);
        }
        AtomicReference<String> savedKey = new AtomicReference<>(defaultID);
        view.openEditorFrame(template, defaultID, initialData, data -> {
            savedKey.set(saveObjectData(data, savedKey.get()));
            onSave.accept(data);
            updateProjectChanges();
        }, data -> validateObject(data, savedKey.get()));
    }

    @Override
    public void onEditObject(String categoryID, String objectID) {
        Template template = templateRegistry.getTemplate(categoryID);
        Data initialData = dataManager.getData(categoryID, objectID);
        AtomicReference<String> savedKey = new AtomicReference<>(objectID);
        view.openEditorFrame(template, objectID, initialData, data -> {
            savedKey.set(saveObjectData(data, savedKey.get()));
            updateProjectChanges();
            }, data -> validateObject(data, savedKey.get()));
    }

    @Override
    public void onEditObject(String categoryID, String objectID, BiConsumer<Data, Data> onSave) {
        Template template = templateRegistry.getTemplate(categoryID);
        Data initialData = dataManager.getData(categoryID, objectID);
        AtomicReference<Data> currentData = new AtomicReference<>(initialData);
        AtomicReference<String> savedKey = new AtomicReference<>(objectID);
        view.openEditorFrame(template, objectID, initialData, data -> {
            savedKey.set(saveObjectData(data, savedKey.get()));
            onSave.accept(data, currentData.get());
            currentData.set(data);
            updateProjectChanges();
        }, data -> validateObject(data, savedKey.get()));
    }

    @Override
    public boolean onDeleteObject(String categoryID, String objectID) {
        Set<Reference> references = getReferences(categoryID, objectID);
        DeleteObjectConfirmationResult result = view.confirmDeleteObject(objectID, references.size());
        if (result == DeleteObjectConfirmationResult.DELETE) {
            dataManager.removeData(categoryID, objectID);
            view.browserRemoveObject(categoryID, objectID);
            updateProjectChanges();
            return true;
        } else if (result == DeleteObjectConfirmationResult.VIEW_REFERENCES) {
            view.openReferenceList(references);
            return false;
        }
        return false;
    }

    @Override
    public void onDuplicateObject(String categoryID, String objectID) {
        Data objectData = dataManager.getData(categoryID, objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateID(objectID, dataManager.getIDsForCategory(categoryID));
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        dataManager.setData(categoryID, newObjectID, objectDataCopy);
        view.browserAddObject(categoryID, newObjectID);
        updateProjectChanges();
    }

    @Override
    public void onShowReferences(String categoryID, String objectID) {
        Set<Reference> references = getReferences(categoryID, objectID);
        view.openReferenceList(references);
    }

    @Override
    public void onOpenReference(String categoryID, String objectID) {
        if (categoryID.isEmpty() && objectID.equals(CONFIG_OBJECT_NAME)) {
            onOpenConfigEditor();
        } else {
            onEditObject(categoryID, objectID);
        }
    }

    @Override
    public void onOpenPhraseMenu() {
        view.openPhraseMenu(phraseEditorManager.getPhrases());
    }

    @Override
    public void onOpenPhrase(String phraseKey) {
        Data initialData = generateDataForPhrase(phraseKey);
        AtomicReference<String> savedKey = new AtomicReference<>(phraseKey);
        view.openPhraseEditor(phraseKey, initialData, data -> {
            savedKey.set(savePhraseData(data, savedKey.get()));
            updateProjectChanges();
        }, data -> validatePhrase(data, savedKey.get()));
    }

    @Override
    public void onNewPhrase() {
        String defaultKey = generateDefaultPhraseKey();
        phraseEditorManager.setPhrase(defaultKey, "");
        view.updatePhrases(phraseEditorManager.getPhrases());
        Data initialData = generateDataForPhrase(defaultKey);
        AtomicReference<String> savedKey = new AtomicReference<>(defaultKey);
        view.openPhraseEditor(defaultKey, initialData, data -> {
            savedKey.set(savePhraseData(data, savedKey.get()));
            updateProjectChanges();
        }, data -> validatePhrase(data, savedKey.get()));
    }

    @Override
    public void onDeletePhrase(String phraseKey) {
        DeleteConfirmationResult result = view.confirmDeletePhrase(phraseKey);
        if (result == DeleteConfirmationResult.DELETE) {
            view.closePhrase(phraseKey);
            phraseEditorManager.removePhrase(phraseKey);
            view.updatePhrases(phraseEditorManager.getPhrases());
            updateProjectChanges();
        }
    }

    @Override
    public void onDuplicatePhrase(String phraseKey) {
        String phraseText = phraseEditorManager.getPhrase(phraseKey);
        String duplicateKey = generateDuplicateID(phraseKey, phraseEditorManager.getPhraseIDs());
        phraseEditorManager.setPhrase(duplicateKey, phraseText);
        view.updatePhrases(phraseEditorManager.getPhrases());
        updateProjectChanges();
    }

    @Override
    public void onOpenScriptMenu() {
        view.openScriptMenu(scriptEditorManager.getScripts());
    }

    @Override
    public void onOpenScript(String scriptName) {
        Data initialData = generateDataForScript(scriptName);
        view.openScriptEditor(scriptName, initialData, data -> {
            String scriptBody = getScriptBodyFromData(data);
            scriptEditorManager.setScript(scriptName, scriptBody);
            view.updateScripts(scriptEditorManager.getScripts());
            updateProjectChanges();
        }, data -> new ErrorData(false, null));
    }

    @Override
    public void onNewScript() {
        String scriptName = view.promptScriptName();
        if (scriptName == null) {
            return;
        } else if (scriptName.trim().isEmpty()) {
            view.showError("Script name cannot be empty.");
            return;
        } else if (scriptEditorManager.hasScriptWithName(scriptName)) {
            view.showError("A script with the name " + scriptName + " already exists.");
            return;
        }
        view.openScriptEditor(scriptName, null, data -> {
            String scriptBody = getScriptBodyFromData(data);
            scriptEditorManager.setScript(scriptName, scriptBody);
            view.updateScripts(scriptEditorManager.getScripts());
            updateProjectChanges();
        }, data -> new ErrorData(false, null));
    }

    @Override
    public void onDeleteScript(String scriptName) {
        DeleteConfirmationResult result = view.confirmDeleteScript(scriptName);
        if (result == DeleteConfirmationResult.DELETE) {
            view.closeScript(scriptName);
            scriptEditorManager.removeScript(scriptName);
            view.updateScripts(scriptEditorManager.getScripts());
            updateProjectChanges();
        }
    }

    @Override
    public void onOpenConfigEditor() {
        Data initialData = configMenuManager.getConfigData();
        AtomicReference<Data> currentData = new AtomicReference<>(initialData);
        view.openConfigEditor(initialData, data -> {
            configMenuManager.setConfigData(data);
            view.updateProjectName(configMenuManager.getProjectName());
            currentData.set(data);
            updateProjectChanges();
        }, data -> validateConfig(data, currentData.get()));
    }

    @Override
    public boolean onCloseProgram() {
        SaveConfirmationResult result = closeProjectWithSaveConfirmation();
        if (result == SaveConfirmationResult.YES) {
            onSaveProject();
            return true;
        } else return result != SaveConfirmationResult.CANCEL;
    }

    @Override
    public void onUndo() {
        // TODO - Implement
    }

    @Override
    public void onRedo() {
        // TODO - Implement
    }

    private SaveConfirmationResult closeProjectWithSaveConfirmation() {
        if (projectHasUnsavedChanges()) {
            SaveConfirmationResult result = view.confirmProjectSave();
            if (result == SaveConfirmationResult.CANCEL) return SaveConfirmationResult.CANCEL;
            if (result == SaveConfirmationResult.YES) {
                view.closeAllEditors();
                return SaveConfirmationResult.YES;
            } else {
                view.closeAllEditors();
                return SaveConfirmationResult.NO;
            }
        }
        return SaveConfirmationResult.NO;
    }

    private void updateProjectChanges() {
        view.setHasUnsavedProjectChanges(projectHasUnsavedChanges());
    }

    private boolean projectHasUnsavedChanges() {
        return projectManager.hasUnsavedChanges() || phraseEditorManager.hasUnsavedChanges() || scriptEditorManager.hasUnsavedChanges() || configMenuManager.hasUnsavedChanges() || dataManager.hasUnsavedChanges();
    }

    private void setProjectChangesSaved() {
        phraseEditorManager.setSavedChanges();
        scriptEditorManager.setSavedChanges();
        configMenuManager.setSavedChanges();
        dataManager.setSavedChanges();
        view.setHasUnsavedProjectChanges(false);
    }

    private Data generateDataForPhrase(String phraseKey) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put(PARAMETER_PHRASE_KEY, new DataString(phraseKey));
        dataMap.put(PARAMETER_PHRASE_TEXT, new DataString(phraseEditorManager.getPhrase(phraseKey)));
        return new DataObject(InternalTemplates.PHRASE_TEMPLATE, dataMap);
    }

    private String getPhraseKeyFromData(Data data) {
        if (data == null) return null;
        return ((DataString) ((DataObject) data).getValue().get(PARAMETER_PHRASE_KEY)).getValue();
    }

    private String getPhraseTextFromData(Data data) {
        if (data == null) return null;
        return ((DataString) ((DataObject) data).getValue().get(PARAMETER_PHRASE_TEXT)).getValue();
    }

    private Data generateDataForScript(String scriptName) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put(PARAMETER_SCRIPT_NAME, new DataString(scriptName));
        dataMap.put(PARAMETER_SCRIPT_BODY, new DataScript(scriptEditorManager.getScript(scriptName)));
        return new DataObject(InternalTemplates.SCRIPT_TEMPLATE, dataMap);
    }

    private String getScriptBodyFromData(Data data) {
        if (data == null) return null;
        return ((DataScript) ((DataObject) data).getValue().get(PARAMETER_SCRIPT_BODY)).getValue();
    }

    private String saveObjectData(Data objectData, String savedKey) {
        if (!(objectData instanceof DataObject objectDataCast)) {
            throw new IllegalArgumentException("Top-level saved data must be an object");
        }
        String newID = objectDataCast.getID();
        String categoryID = objectDataCast.getTemplate().id();
        boolean newIDInvalid = newID == null || newID.trim().isEmpty();
        boolean idChanged = !Objects.equals(savedKey, newID);
        boolean collision = idChanged && !newIDInvalid && dataManager.categoryContainsID(categoryID, newID);
        String targetKey = (collision || newIDInvalid) ? savedKey : newID;
        dataManager.setData(categoryID, targetKey, objectData);
        if (idChanged && !collision && !newIDInvalid) {
            dataManager.removeData(categoryID, savedKey);
            if (objectDataCast.getTemplate().topLevel()) {
                view.browserRemoveObject(categoryID, savedKey);
                view.browserAddObject(categoryID, targetKey);
            }
            renameReferences(categoryID, savedKey, targetKey);
        }
        return targetKey;
    }

    private String savePhraseData(Data data, String savedKey) {
        String newKey = getPhraseKeyFromData(data);
        String newText = getPhraseTextFromData(data);
        boolean newKeyInvalid = newKey == null || newKey.trim().isEmpty();
        boolean keyChanged = !Objects.equals(savedKey, newKey);
        boolean collision = keyChanged && !newKeyInvalid && phraseEditorManager.hasPhraseWithKey(newKey);
        String targetKey = (collision || newKeyInvalid) ? savedKey : newKey;

        phraseEditorManager.setPhrase(targetKey, newText);
        if (keyChanged && !collision && !newKeyInvalid) {
            phraseEditorManager.removePhrase(savedKey);
        }
        view.updatePhrases(phraseEditorManager.getPhrases());
        return targetKey;
    }

    private String getObjectIDFromData(Data data) {
        if (data == null) return null;
        return ((DataObject) data).getID();
    }

    private String getObjectCategoryIDFromData(Data data) {
        if (data == null) return null;
        return ((DataObject) data).getTemplate().id();
    }

    private ErrorData validateObject(Data currentData, String savedKey) {
        String currentObjectID = getObjectIDFromData(currentData);
        String categoryID = getObjectCategoryIDFromData(currentData);
        if (currentObjectID == null || currentObjectID.trim().isEmpty()) {
            return new ErrorData(true, "Object ID cannot be empty.");
        } else if (!Objects.equals(currentObjectID, savedKey) && dataManager.categoryContainsID(categoryID, currentObjectID)) {
            return new ErrorData(true, "An object with ID \"" + currentObjectID + "\" already exists.");
        }
        return new ErrorData(false, null);
    }

    private ErrorData validatePhrase(Data currentData, String savedKey) {
        String newKey = getPhraseKeyFromData(currentData);
        if (newKey.trim().isEmpty()) {
            return new ErrorData(true, "Key cannot be empty.");
        }
        String newPhrase = getPhraseTextFromData(currentData);
        if (newPhrase.trim().isEmpty()) {
            return new ErrorData(true, "Phrase cannot be empty.");
        }
        if (!Objects.equals(newKey, savedKey) && phraseEditorManager.hasPhraseWithKey(newKey)) {
            return new ErrorData(true, "A phrase with the key " + newKey + " already exists.");
        }
        return new ErrorData(false, null);
    }

    private ErrorData validateConfig(Data currentData, Data initialData) {
        String currentProjectName = configMenuManager.getProjectNameFromData(currentData);
        if (currentProjectName == null || currentProjectName.trim().isEmpty()) {
            return new ErrorData(true, "Game name cannot be empty.");
        }
        return new ErrorData(false, null);
    }

    private String generateDuplicateID(String originalID, Set<String> existingIDSet) {
        String baseCopyID = originalID + "_COPY_";
        int i = 1;
        while (existingIDSet.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

    private String generateDefaultID(String categoryID, Template template) {
        String base = "NEW_" + template.id();
        int i = 1;
        while (dataManager.categoryContainsID(categoryID, base + "_" + i)) {
            i += 1;
        }
        return base + "_" + i;
    }

    private String generateDefaultPhraseKey() {
        String base = "NEW_PHRASE";
        int i = 1;
        while (phraseEditorManager.hasPhraseWithKey(base + "_" + i)) {
            i += 1;
        }
        return base + "_" + i;
    }

    private Set<Reference> getReferences(String categoryID, String objectID) {
        Set<Reference> references = dataManager.findReferences(categoryID, objectID);
        Reference configReference = configMenuManager.findReference(categoryID, objectID);
        if (configReference != null) {
            references.add(configReference);
        }
        return references;
    }

    private void renameReferences(String categoryID, String objectID, String newObjectID) {
        configMenuManager.renameReferences(categoryID, objectID, newObjectID);
        dataManager.renameReferences(categoryID, objectID, newObjectID);
    }

}
