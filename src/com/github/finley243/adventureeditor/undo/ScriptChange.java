package com.github.finley243.adventureeditor.undo;

public record ScriptChange(String scriptName, String before, String after) implements DataChange {
}
