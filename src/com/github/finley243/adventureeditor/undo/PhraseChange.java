package com.github.finley243.adventureeditor.undo;

import com.github.finley243.adventureeditor.data.Data;

public record PhraseChange(String beforeKey, String afterKey, Data before, Data after) implements DataChange {
}
