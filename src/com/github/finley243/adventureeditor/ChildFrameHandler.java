package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import java.util.*;

public class ChildFrameHandler<T> {

    private final Map<T, EditorFrame> activeEditorFrames;
    private final List<EditorFrame> activeEditorFramesUnsaved;

    public ChildFrameHandler() {
        this.activeEditorFrames = new HashMap<>();
        this.activeEditorFramesUnsaved = new ArrayList<>();
    }

    public void add(T key, EditorFrame frame) {
        if (key != null) {
            activeEditorFrames.put(key, frame);
        } else {
            activeEditorFramesUnsaved.add(frame);
        }
    }

    public EditorFrame get(T key) {
        return activeEditorFrames.get(key);
    }

    public boolean requestFocusIfOpen(T key) {
        if (key == null) return false;
        if (activeEditorFrames.containsKey(key)) {
            EditorFrame frame = activeEditorFrames.get(key);
            frame.toFront();
            frame.requestFocus();
            return true;
        }
        return false;
    }

    public boolean removeChildFrame(EditorFrame frame) {
        if (activeEditorFramesUnsaved.contains(frame)) {
            activeEditorFramesUnsaved.remove(frame);
            return true;
        } else {
            Iterator<Map.Entry<T, EditorFrame>> iterator = activeEditorFrames.entrySet().iterator();
            while (iterator.hasNext()) {
                EditorFrame entryValue = iterator.next().getValue();
                if (entryValue.equals(frame)) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean closeAll() {
        // These MUST be while-loops to prevent concurrent modification exceptions
        while (activeEditorFrames.values().iterator().hasNext()) {
            EditorFrame editorFrame = activeEditorFrames.values().iterator().next();
            boolean didClose = editorFrame.requestClose(false, false);
            if (!didClose) return false;
        }
        while (!activeEditorFramesUnsaved.isEmpty()) {
            EditorFrame editorFrame = activeEditorFramesUnsaved.getFirst();
            boolean didClose = editorFrame.requestClose(false, false);
            if (!didClose) return false;
        }
        return true;
    }

}
