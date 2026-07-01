package com.github.finley243.adventureeditor.undo;

public record ScriptCreate(String scriptName, String text) implements DataChange {
}
