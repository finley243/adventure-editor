package com.github.finley243.adventureeditor.validation;

public sealed interface ValidationIssue permits ObjectValidationIssue, PhraseValidationIssue, ConfigValidationIssue, ScriptValidationIssue {

    String message();

    ValidationSeverity severity();

}
