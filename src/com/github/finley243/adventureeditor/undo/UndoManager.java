package com.github.finley243.adventureeditor.undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {

    private final Deque<DataChangeCommand> undoStack;
    private final Deque<DataChangeCommand> redoStack;

    public UndoManager() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void pushChange(DataChangeCommand command) {
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public DataChangeCommand undo() {
        DataChangeCommand command = undoStack.pop();
        redoStack.push(command);
        return command;
    }

    public DataChangeCommand redo() {
        DataChangeCommand command = redoStack.pop();
        undoStack.push(command);
        return command;
    }

}
