package com.github.finley243.adventureeditor.validation;

public record ScriptValidationIssue(String scriptName, String message, ValidationSeverity severity) implements ValidationIssue {
}
