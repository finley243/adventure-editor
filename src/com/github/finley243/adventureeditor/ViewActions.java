package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ViewActions {

    void browserAddObject(String categoryID, String objectID);

    void browserRemoveObject(String categoryID, String objectID);

    void reregisterObject(String categoryID, String oldObjectID, String newObjectID);

    void browserClear();

    void browserLoadObjects(Map<String, Set<String>> objects);

    void openConfigEditor(Data initialData, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    EditorSession openEditorFrame(Template template, String objectID, Data initialData, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    void openPhraseMenu(Map<String, String> phrases);

    EditorSession openPhraseEditor(String phraseKey, Data content, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    void updatePhrases(Map<String, String> phrases);

    void openScriptMenu(Map<String, String> scripts);

    void openScriptEditor(String name, Data content, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    String promptScriptName();

    void updateScripts(Map<String, String> scripts);

    void openReferenceList(Set<Reference> references);

    void showError(String message);

    SaveConfirmationResult confirmProjectSave();

    DeleteConfirmationResult confirmDeletePhrase(String deleteName);

    DeleteConfirmationResult confirmDeleteScript(String deleteName);

    DeleteObjectConfirmationResult confirmDeleteObject(String objectID, int referenceCount);

    File selectSaveDirectory();

    void setProjectIsLoaded(boolean isProjectLoaded);

    void updateProjectName(String name);

    void setHasUnsavedProjectChanges(boolean hasUnsaved);

    void updateRecentProjects(List<ProjectFile> recentProjects);

    void closeObject(String categoryID, String objectID);

    void forceCloseConfig();

    void closeScript(String name);

    void closePhrase(String key);

    boolean hasOpenEditors();

    void closeAllEditors();

    void updateUndoRedoButtons(boolean canUndo, boolean canRedo);

    void refreshConfigEditor(Data data);

    void refreshObjectEditor(String categoryID, String fromKey, String toKey, Data data);

    void refreshPhraseEditor(String fromKey, String toKey, Data data);

    void refreshScriptEditor(String scriptName, Data data);

}
