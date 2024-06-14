package com.github.finley243.adventureeditor.template;

import java.util.List;

public record TemplateParameter(String id, ParameterDataType dataType, String name, String type, List<String> enumOptions, boolean topLevelOnly, boolean optional, ParameterFormat format) {

    /*
     * dataType is the type of parameter
     * name is the name of the parameter
     * type is the type of object or reference (unused for other data types)
     * topLevelOnly determines whether the parameter is shown when template is used as a nested object
     */

    public enum ParameterDataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET, OBJECT, OBJECT_SET, REFERENCE, ENUM
    }

    public enum ParameterFormat {
        ATTRIBUTE, CHILD_TAG, CURRENT_TAG
    }

}
