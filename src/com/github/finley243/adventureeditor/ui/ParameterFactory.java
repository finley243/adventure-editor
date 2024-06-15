package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.template.TemplateParameter;

public class ParameterFactory {

    public static EditorElement create(TemplateParameter parameter, Main main, EditorFrame editorFrame) {
        return switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(editorFrame, parameter.name());
            case INTEGER -> new ParameterFieldInteger(editorFrame, parameter.name());
            case FLOAT -> new ParameterFieldFloat(editorFrame, parameter.name());
            case STRING -> new ParameterFieldString(editorFrame, parameter.name());
            case STRING_SET -> new ParameterFieldStringSet(editorFrame, parameter.name());
            case OBJECT -> new ParameterFieldObject(editorFrame, parameter.name(), main.getTemplate(parameter.type()), main, false);
            case OBJECT_SET -> new ParameterFieldObjectSet(editorFrame, parameter.name(), main.getTemplate(parameter.type()), main);
            case REFERENCE -> new ParameterFieldReference(editorFrame, parameter.name(), main.getIDsForCategory(parameter.type()) == null || main.getIDsForCategory(parameter.type()).isEmpty() ? new String[0] : main.getIDsForCategory(parameter.type()).toArray(new String[0]));
            case ENUM -> new ParameterFieldEnum(editorFrame, parameter.name(), parameter.enumOptions().toArray(new String[0]));
        };
    }

}
