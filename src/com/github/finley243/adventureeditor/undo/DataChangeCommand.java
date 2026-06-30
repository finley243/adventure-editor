package com.github.finley243.adventureeditor.undo;

import java.util.List;

public record DataChangeCommand(List<DataChange> dataChanges) {
}
