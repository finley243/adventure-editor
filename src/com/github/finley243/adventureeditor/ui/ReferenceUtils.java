package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

public class ReferenceUtils {

    public static boolean dataContainsReference(Data data, String categoryID, String objectID) {
        if (!(data instanceof DataObject dataObject)) {
            throw new IllegalArgumentException("Data must be an object");
        }
        Template template = dataObject.getTemplate();
        for (TemplateParameter parameter : template.parameters()) {
            Data innerData = dataObject.getValue().get(parameter.id());
            if (innerData instanceof DataReference innerReference) {
                if (parameter.type().equals(categoryID) && innerReference.getValue().equals(objectID)) {
                    return true;
                }
            } else if (innerData instanceof DataReferenceSet innerReferenceSet) {
                if (parameter.type().equals(categoryID) && innerReferenceSet.getValue().contains(objectID)) {
                    return true;
                }
            } else if (innerData instanceof DataObject innerObject) {
                if (dataContainsReference(innerObject, categoryID, objectID)) {
                    return true;
                }
            } else if (innerData instanceof DataObjectSet innerObjectSet) {
                for (Data innerObject : innerObjectSet.getValue()) {
                    if (dataContainsReference(innerObject, categoryID, objectID)) {
                        return true;
                    }
                }
            } else if (innerData instanceof DataComponent innerComponent) {
                if (dataContainsReference(innerComponent.getObjectData(), categoryID, objectID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void renameReferencesInData(Data data, String categoryID, String objectID, String newObjectID) {
        if (!(data instanceof DataObject dataObject)) {
            throw new IllegalArgumentException("Data must be an object");
        }
        Template template = dataObject.getTemplate();
        for (TemplateParameter parameter : template.parameters()) {
            Data innerData = dataObject.getValue().get(parameter.id());
            if (innerData instanceof DataReference innerReference) {
                if (parameter.type().equals(categoryID) && innerReference.getValue().equals(objectID)) {
                    dataObject.replaceValue(parameter.id(), new DataReference(newObjectID));
                }
            } else if (innerData instanceof DataReferenceSet innerReferenceSet) {
                if (parameter.type().equals(categoryID)) {
                    int indexOfReference = innerReferenceSet.getValue().indexOf(objectID);
                    if (indexOfReference != -1) {
                        innerReferenceSet.getValue().set(indexOfReference, newObjectID);
                    }
                }
            } else if (innerData instanceof DataObject innerObject) {
                renameReferencesInData(innerObject, categoryID, objectID, newObjectID);
            } else if (innerData instanceof DataObjectSet innerObjectSet) {
                for (Data innerObject : innerObjectSet.getValue()) {
                    renameReferencesInData(innerObject, categoryID, objectID, newObjectID);
                }
            } else if (innerData instanceof DataComponent innerComponent) {
                renameReferencesInData(innerComponent.getObjectData(), categoryID, objectID, newObjectID);
            }
        }
    }

}
