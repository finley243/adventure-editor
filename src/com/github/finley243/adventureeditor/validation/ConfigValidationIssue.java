package com.github.finley243.adventureeditor.validation;

public record ConfigValidationIssue(String message, ValidationSeverity severity) implements ValidationIssue {
}
