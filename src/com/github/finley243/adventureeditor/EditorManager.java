package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EditorManager {

    private final Main main;
    private final Map<String, Map<String, EditorFrame>> topLevelEditorWindows;

    public EditorManager(Main main) {
        this.main = main;
        this.topLevelEditorWindows = new HashMap<>();
    }

    public void openEditorFrame(String categoryID, String objectID, Template template, Data objectData, DataSaveTarget saveTargetOverride) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.toFront();
            activeFrame.requestFocus();
        } else {
            EditorFrame editorFrame = new EditorFrame(main, null, main.getMainFrame(), template, objectData, saveTargetOverride != null ? saveTargetOverride : main.getMainFrame(), true);
            addActiveTopLevelFrame(categoryID, objectID, editorFrame);
        }
    }

    public void closeEditorFrameIfActive(String categoryID, String objectID) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.dispose();
            removeActiveTopLevelFrame(categoryID, objectID);
        }
    }

    public void closeAllActiveEditorFrames() {
        for (String categoryID : new HashSet<>(topLevelEditorWindows.keySet())) {
            for (String objectID : new HashSet<>(topLevelEditorWindows.get(categoryID).keySet())) {
                topLevelEditorWindows.get(categoryID).get(objectID).requestClose(false, false);
            }
        }
        topLevelEditorWindows.clear();
    }

    private EditorFrame getActiveTopLevelFrame(String categoryID, String objectID) {
        if (categoryID == null | objectID == null) {
            return null;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            return null;
        }
        return topLevelEditorWindows.get(categoryID).get(objectID);
    }

    private void addActiveTopLevelFrame(String categoryID, String objectID, EditorFrame frame) {
        if (objectID == null) {
            return;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            topLevelEditorWindows.put(categoryID, new HashMap<>());
        }
        topLevelEditorWindows.get(categoryID).put(objectID, frame);
    }

    private void removeActiveTopLevelFrame(String categoryID, String objectID) {
        if (objectID == null) {
            return;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            return;
        }
        topLevelEditorWindows.get(categoryID).remove(objectID);
        if (topLevelEditorWindows.get(categoryID).isEmpty()) {
            topLevelEditorWindows.remove(categoryID);
        }
    }

}
