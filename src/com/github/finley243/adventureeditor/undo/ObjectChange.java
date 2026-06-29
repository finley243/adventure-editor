package com.github.finley243.adventureeditor.undo;

import com.github.finley243.adventureeditor.data.Data;

public record ObjectChange(String categoryID, String objectID, Data before, Data after) {
}
