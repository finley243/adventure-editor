package com.github.finley243.adventureeditor.template;

import java.util.List;

public record Template(String id, String name, boolean topLevel, List<TemplateParameter> parameters) {
}
