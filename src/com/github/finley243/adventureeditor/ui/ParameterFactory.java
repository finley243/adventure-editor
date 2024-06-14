package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import java.util.Map;

public class ParameterFactory {

    public static EditorElement create(TemplateParameter parameter, Map<String, Template> templates, Map<String, Map<String, Data>> data) {
        return switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(parameter.name());
            case INTEGER -> new ParameterFieldInteger(parameter.name());
            case FLOAT -> new ParameterFieldFloat(parameter.name());
            case STRING -> new ParameterFieldString(parameter.name());
            case STRING_SET -> new ParameterFieldStringSet(parameter.name());
            case OBJECT -> new ParameterFieldObject(parameter.name(), templates.get(parameter.type()), templates, data, false);
            case OBJECT_SET -> new ParameterFieldObjectSet(parameter.name(), templates.get(parameter.type()), templates, data);
            case REFERENCE -> new ParameterFieldReference(parameter.name(), !data.containsKey(parameter.type()) || data.get(parameter.type()).keySet().isEmpty() ? new String[0] : data.get(parameter.type()).keySet().toArray(new String[0]));
            case ENUM -> new ParameterFieldEnum(parameter.name(), parameter.enumOptions().toArray(new String[0]));
        };
    }

}
