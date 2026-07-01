package com.github.finley243.adventureeditor.undo;

public record PhraseCreate(String key, String text) implements DataChange {
}
