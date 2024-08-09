package com.github.finley243.adventureeditor.template;

import com.github.finley243.adventureeditor.data.Data;

import java.util.List;

/**
 * Represents a parameter in a template.
 * @param id The name of the parameter as it appears in the game data (must be unique within the template).
 * @param dataType The type of data that the parameter represents.
 * @param name The name of the parameter as it appears in the editor.
 * @param type For object or object set parameter, a template ID. For enum parameter, an enum type ID.
 * @param topLevelOnly Whether the parameter should only be shown when editing a top-level object instance.
 * @param optional Whether the parameter can be disabled by the user.
 * @param format The format of the parameter.
 * @param group The group that the parameter belongs to.
 * @param x The GridBag x position of the parameter in the editor window (or in the specified group).
 * @param y The GridBag y position of the parameter in the editor window (or in the specified group).
 * @param width The GridBag width of the parameter in the editor window (or in the specified group).
 * @param height The GridBag height of the parameter in the editor window (or in the specified group).
 */
public record TemplateParameter(String id, ParameterDataType dataType, String name, String type, boolean topLevelOnly, boolean optional, ParameterFormat format, ComponentFormat componentFormat, List<ComponentOption> componentOptions, boolean useComponentTypeName, String group, int x, int y, int width, int height, Data defaultValue) {

    public enum ParameterDataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_LONG, OBJECT, OBJECT_SET, OBJECT_SET_UNIQUE, REFERENCE, REFERENCE_SET, ENUM, SCRIPT, COMPONENT, TREE, TREE_BRANCH
    }

    public enum ParameterFormat {
        ATTRIBUTE, CHILD_TAG, CURRENT_TAG
    }

    public enum ComponentFormat {
        TYPE_ATTRIBUTE, TEXT_OR_TAGS
    }

}
