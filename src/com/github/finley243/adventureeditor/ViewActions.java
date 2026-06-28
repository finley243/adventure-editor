package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DeleteConfirmationResult;
import com.github.finley243.adventureeditor.ui.DeleteObjectConfirmationResult;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ViewActions {

    void browserAddObject(String categoryID, String objectID);

    void browserRemoveObject(String categoryID, String objectID);

    void browserClear();

    void browserLoadObjects(Map<String, Set<String>> objects);

    void openConfigEditor(Data initialData, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    void openEditorFrame(Template template, String objectID, Data initialData, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    void openPhraseMenu(Map<String, String> phrases);

    void openPhraseEditor(String phraseKey, Data content, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

    void updatePhrases(Map<String, String> phrases);

    void openScriptMenu(Map<String, String> scripts);

    void openScriptEditor(String name, Data content, Consumer<Data> onSave, Function<Data, ErrorData> onValidate);

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

    void forceCloseObject(String categoryID, String objectID);

    void forceCloseConfig();

    void forceCloseScript(String name);

    void forceClosePhrase(String key);

    void forceCloseAllEditors();

    boolean hasOpenEditors();

    boolean closeAllEditorsWithConfirmation();

}
