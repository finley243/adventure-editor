package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.DataManager;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

public class ParameterFieldFactory {

    private final TemplateRegistry templateRegistry;
    private final DataManager dataManager;
    private final DataSaveTarget topLevelSaveTarget;

    public ParameterFieldFactory(TemplateRegistry templateRegistry, DataManager dataManager, DataSaveTarget topLevelSaveTarget) {
        this.templateRegistry = templateRegistry;
        this.dataManager = dataManager;
        this.topLevelSaveTarget = topLevelSaveTarget;
    }

    public ParameterField create(TemplateParameter parameter, EditorFrame editorFrame, ParameterField parentField) {
        ParameterField parameterElement = switch (parameter.dataType()) {
            case BOOLEAN -> new ParameterFieldBoolean(editorFrame, parameter.optional(), parameter.name(), parentField);
            case INTEGER -> new ParameterFieldInteger(editorFrame, parameter.optional(), parameter.name(), parentField);
            case FLOAT -> new ParameterFieldFloat(editorFrame, parameter.optional(), parameter.name(), parentField);
            case STRING -> new ParameterFieldString(editorFrame, parameter.optional(), parameter.name(), parentField);
            case STRING_LONG -> new ParameterFieldText(editorFrame, parameter.optional(), parameter.name(), parentField);
            case OBJECT -> new ParameterFieldObject(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getTemplate(parameter.type()), false, this);
            case OBJECT_SET -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getTemplate(parameter.type()), false);
            case OBJECT_SET_UNIQUE -> new ParameterFieldObjectSet(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getTemplate(parameter.type()), true);
            case REFERENCE -> new ParameterFieldReference(editorFrame, parameter.optional(), parameter.name(), parentField, parameter.type(), dataManager.getIDsForCategoryArray(parameter.type()), dataManager, topLevelSaveTarget);
            case REFERENCE_SET -> new ParameterFieldReferenceSet(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getTemplate(parameter.type()), dataManager, topLevelSaveTarget);
            case ENUM -> new ParameterFieldEnum(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getEnumValuesArray(parameter.type()));
            case SCRIPT -> new ParameterFieldScript(editorFrame, parameter.optional(), parameter.name(), parentField);
            case COMPONENT -> new ParameterFieldComponent(editorFrame, parameter.optional(), parameter.name(), parentField, parameter.componentOptions(), templateRegistry.getTemplatesForComponents(parameter.componentOptions()), this);
            case TREE -> new ParameterFieldTree(editorFrame, parameter.optional(), parameter.name(), parentField, templateRegistry.getTemplate(parameter.type()), parameter.id());
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
