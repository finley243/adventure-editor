package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.EditorFrame;

public class ParameterFieldFactory {

    public static ParameterField create(TemplateParameter parameter, Main main, EditorFrame editorFrame) {
        ParameterField parameterElement = switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(editorFrame, parameter.optional(), parameter.name());
            case INTEGER -> new ParameterFieldInteger(editorFrame, parameter.optional(), parameter.name());
            case FLOAT -> new ParameterFieldFloat(editorFrame, parameter.optional(), parameter.name());
            case STRING -> new ParameterFieldString(editorFrame, parameter.optional(), parameter.name(), false);
            case STRING_LONG -> new ParameterFieldString(editorFrame, parameter.optional(), parameter.name(), true);
            case OBJECT -> new ParameterFieldObject(editorFrame, parameter.optional(), parameter.name(), main.getTemplate(parameter.type()), main, false, false);
            case OBJECT_SET -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), main.getTemplate(parameter.type()), false, main);
            case OBJECT_SET_UNIQUE -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), main.getTemplate(parameter.type()), true, main);
            case REFERENCE -> new ParameterFieldReference(editorFrame, parameter.optional(), parameter.name(), main, parameter.type());
            case REFERENCE_SET -> new ParameterFieldReferenceSet(editorFrame, parameter.optional(), parameter.name(), main.getTemplate(parameter.type()), main);
            case ENUM -> new ParameterFieldEnum(editorFrame, parameter.optional(), parameter.name(), main.getEnumValues(parameter.type()).toArray(new String[0]));
            case SCRIPT -> new ParameterFieldScript(editorFrame, parameter.optional(), parameter.name());
            case COMPONENT -> new ParameterFieldComponent(editorFrame, parameter.optional(), parameter.name(), parameter.componentFormat(), parameter.componentOptions(), parameter.useComponentTypeName(), main);
            case TREE -> new ParameterFieldTree(editorFrame, parameter.optional(), parameter.name(), main.getTemplate(parameter.type()), parameter.id(), main);
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
