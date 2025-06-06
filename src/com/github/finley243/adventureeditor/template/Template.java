package com.github.finley243.adventureeditor.template;

import java.util.List;

public record Template(String id, String name, boolean topLevel, boolean unique, List<Group> groups, List<TabGroup> tabGroups, List<TemplateParameter> parameters, String nameFormat, String primaryParameter) {
}
