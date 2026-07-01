package com.github.finley243.adventureeditor.undo;

public sealed interface DataChange permits ConfigChange, ObjectChange, ObjectCreate, ObjectDelete, PhraseChange, PhraseCreate, PhraseDelete, ScriptChange, ScriptCreate, ScriptDelete {
}
