package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.SaveConfirmationResult;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ViewActions {

    void openEditorFrame(String editorID, Template template, Data data, BiConsumer<Data, Data> onSave, Function<Data, DataSaveTarget.ErrorData> onValidate);

    void openPhraseEditor(Map<String, String> phrases, Consumer<Map<String, String>> onSave);

    void openScriptEditor(Map<String, String> scripts, Consumer<Map<String, String>> onSave);

    void showError(String message);

    SaveConfirmationResult confirmProjectSave();

    File selectSaveDirectory();

    void setProjectIsLoaded(boolean isProjectLoaded);

    void updateProjectName(String name);

    void setUnsavedChanges(boolean hasUnsaved);

    void updateRecentProjects(List<ProjectFile> recentProjects);

}
