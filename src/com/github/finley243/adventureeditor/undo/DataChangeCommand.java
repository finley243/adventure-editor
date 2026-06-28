package com.github.finley243.adventureeditor.undo;

import com.github.finley243.adventureeditor.data.Data;

import java.util.List;

public record DataChangeCommand(List<ObjectChangeCommand> objectChangeCommands) {
    public record ObjectChangeCommand(String categoryID, String objectID, Data before, Data after) {}
}
