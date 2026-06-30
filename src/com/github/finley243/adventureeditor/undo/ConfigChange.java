package com.github.finley243.adventureeditor.undo;

import com.github.finley243.adventureeditor.data.Data;

public record ConfigChange(Data before, Data after) implements DataChange {
}
