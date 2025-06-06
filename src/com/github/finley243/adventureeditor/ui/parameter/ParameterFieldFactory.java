package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

public class ParameterFieldFactory {

    public static ParameterField create(TemplateParameter parameter, Main main, EditorFrame editorFrame, ParameterField parentField) {
        ParameterField parameterElement = switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(editorFrame, parameter.optional(), parameter.name(), parentField);
            case INTEGER -> new ParameterFieldInteger(editorFrame, parameter.optional(), parameter.name(), parentField);
            case FLOAT -> new ParameterFieldFloat(editorFrame, parameter.optional(), parameter.name(), parentField);
            case STRING -> new ParameterFieldString(editorFrame, parameter.optional(), parameter.name(), parentField);
            case STRING_LONG -> new ParameterFieldText(editorFrame, parameter.optional(), parameter.name(), parentField);
            case OBJECT -> new ParameterFieldObject(editorFrame, parameter.optional(), parameter.name(), parentField, main.getTemplate(parameter.type()), main, false);
            case OBJECT_SET -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), parentField, main.getTemplate(parameter.type()), false, main);
            case OBJECT_SET_UNIQUE -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), parentField, main.getTemplate(parameter.type()), true, main);
            case REFERENCE -> new ParameterFieldReference(editorFrame, parameter.optional(), parameter.name(), parentField, main, parameter.type());
            case REFERENCE_SET -> new ParameterFieldReferenceSet(editorFrame, parameter.optional(), parameter.name(), parentField, main.getTemplate(parameter.type()), main);
            case ENUM -> new ParameterFieldEnum(editorFrame, parameter.optional(), parameter.name(), parentField, main.getEnumValues(parameter.type()).toArray(new String[0]));
            case SCRIPT -> new ParameterFieldScript(editorFrame, parameter.optional(), parameter.name(), parentField);
            case COMPONENT -> new ParameterFieldComponent(editorFrame, parameter.optional(), parameter.name(), parentField, parameter.componentOptions(), main);
            case TREE -> new ParameterFieldTree(editorFrame, parameter.optional(), parameter.name(), parentField, main.getTemplate(parameter.type()), parameter.id(), main);
            case TREE_BRANCH -> null;
        };
        if (parameterElement == null) {
            return null;
        }
        if (parameter.defaultValue() != null && !parameter.optional()) {
            parameterElement.setData(parameter.defaultValue());
        }
        return parameterElement;
    }

}
