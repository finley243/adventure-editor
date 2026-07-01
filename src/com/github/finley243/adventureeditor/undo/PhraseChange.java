package com.github.finley243.adventureeditor.undo;

public record PhraseChange(String beforeKey, String afterKey, String beforeText, String afterText) implements DataChange {
}
