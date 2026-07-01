package com.github.finley243.adventureeditor.undo;

public record PhraseDelete(String key, String text) implements DataChange {
}
