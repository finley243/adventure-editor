package com.github.finley243.adventureeditor.validation;

public record ObjectValidationIssue(String categoryID, String key, String message, ValidationSeverity severity) implements ValidationIssue {
}
