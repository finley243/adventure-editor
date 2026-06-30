package com.github.finley243.adventureeditor.undo;

public sealed interface DataChange permits ObjectChange, PhraseChange, ScriptChange, ConfigChange {
}
