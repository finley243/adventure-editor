package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import java.util.Map;

public class ParameterFactory {

    public static EditorElement create(TemplateParameter parameter, Main main) {
        return switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(parameter.name());
            case INTEGER -> new ParameterFieldInteger(parameter.name());
            case FLOAT -> new ParameterFieldFloat(parameter.name());
            case STRING -> new ParameterFieldString(parameter.name());
            case STRING_SET -> new ParameterFieldStringSet(parameter.name());
            case OBJECT -> new ParameterFieldObject(parameter.name(), main.getTemplate(parameter.type()), main, false);
            case OBJECT_SET -> new ParameterFieldObjectSet(parameter.name(), main.getTemplate(parameter.type()), main);
            case REFERENCE -> new ParameterFieldReference(parameter.name(), main.getIDsForCategory(parameter.type()) == null || main.getIDsForCategory(parameter.type()).isEmpty() ? new String[0] : main.getIDsForCategory(parameter.type()).toArray(new String[0]));
            case ENUM -> new ParameterFieldEnum(parameter.name(), parameter.enumOptions().toArray(new String[0]));
        };
    }

}
