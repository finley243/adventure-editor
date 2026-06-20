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

    void onShowReferences(String categoryID, String objectID);

    void onOpenReference(String categoryID, String objectID);

    void onOpenPhraseMenu();

    void onOpenPhrase(String phraseKey);

    void onNewPhrase();

    void onDeletePhrase(String phraseKey);

    void onDuplicatePhrase(String phraseKey);

    void onOpenScriptMenu();

    void onOpenScript(String scriptName);

    void onNewScript();

    void onDeleteScript(String scriptName);

    void onOpenConfigEditor();

}
