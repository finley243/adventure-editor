package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorManager {

    private final Map<String, Map<String, EditorFrame>> topLevelEditorWindows;

    public EditorManager() {
        this.topLevelEditorWindows = new HashMap<>();
    }

    public boolean hasAnyOpenFrame() {
        return topLevelEditorWindows.values().stream().anyMatch(m -> !m.isEmpty());
    }

    public void closeEditorFrameIfActive(String categoryID, String objectID) {
        EditorFrame activeFrame = getActiveTopLevelFrame(categoryID, objectID);
        if (activeFrame != null) {
            activeFrame.dispose();
            removeActiveTopLevelFrame(categoryID, objectID);
        }
    }

    public void requestCloseAllEditorFrames() {
        List<EditorFrame> frames = topLevelEditorWindows.values().stream()
                .flatMap(m -> m.values().stream())
                .toList();
        for (EditorFrame editorFrame : frames) {
            editorFrame.requestClose();
        }
        topLevelEditorWindows.clear();
    }

    public EditorFrame getActiveTopLevelFrame(String categoryID, String objectID) {
        if (categoryID == null || objectID == null) {
            return null;
        }
        if (!topLevelEditorWindows.containsKey(categoryID)) {
            return null;
        }
        return topLevelEditorWindows.get(categoryID).get(objectID);
    }

    public void addActiveTopLevelFrame(String categoryID, String objectID, EditorFrame frame) {
        if (objectID == null) {
            return;
        }
        topLevelEditorWindows.computeIfAbsent(categoryID, k -> new HashMap<>()).put(objectID, frame);
    }

    public void removeActiveTopLevelFrame(String categoryID, String objectID) {
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
