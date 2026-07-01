package com.github.finley243.adventureeditor.validation;

public record PhraseValidationIssue(String key, String message, ValidationSeverity severity) implements ValidationIssue {
}
