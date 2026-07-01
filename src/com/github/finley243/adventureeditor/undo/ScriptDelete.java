package com.github.finley243.adventureeditor.undo;

public record ScriptDelete(String scriptName, String text) implements DataChange {
}
