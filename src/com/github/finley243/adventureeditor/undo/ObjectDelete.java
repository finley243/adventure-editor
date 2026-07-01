package com.github.finley243.adventureeditor.undo;

import com.github.finley243.adventureeditor.data.Data;

public record ObjectDelete(String categoryID, String key, Data data) implements DataChange {
}
