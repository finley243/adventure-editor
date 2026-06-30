package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import java.util.*;

public class ChildFrameHandler<T> {

    private final Map<T, EditorFrame> activeEditorFrames;

    public ChildFrameHandler() {
        this.activeEditorFrames = new HashMap<>();
    }

    public void add(T key, EditorFrame frame) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        activeEditorFrames.put(key, frame);
    }

    public EditorFrame get(T key) {
        return activeEditorFrames.get(key);
    }

    public boolean hasAnyOpenFrame() {
        return !activeEditorFrames.isEmpty();
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
        Iterator<Map.Entry<T, EditorFrame>> iterator = activeEditorFrames.entrySet().iterator();
        while (iterator.hasNext()) {
            EditorFrame entryValue = iterator.next().getValue();
            if (entryValue.equals(frame)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void closeAll() {
        for (EditorFrame editorFrame : new ArrayList<>(activeEditorFrames.values())) {
            editorFrame.requestClose();
        }
    }

}
