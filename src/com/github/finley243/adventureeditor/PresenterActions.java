package com.github.finley243.adventureeditor;

import java.io.File;

public interface PresenterActions {

    void onNewProject();

    void onOpenProject(File file);

    void onSaveProject();

    void onSaveProjectAs(File file);

    void onRemoveRecentProject(ProjectFile projectFile);

    void onClearRecentProjects();

    void onCreateObject(String categoryID);

    void onEditObject(String categoryID, String objectID);

    void onDeleteObject(String categoryID, String objectID);

    void onDuplicateObject(String categoryID, String objectID);

    void onOpenPhraseEditor();

    void onOpenScriptEditor();

    void onOpenConfigEditor();

}
