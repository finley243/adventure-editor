package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;

import javax.swing.*;
import java.util.*;

public class DataManager {

    private final Main main;
    private final Map<String, Map<String, Data>> data;

    public DataManager(Main main) {
        this.main = main;
        this.data = new HashMap<>();
    }

    public Set<String> getIDsForCategory(String categoryID) {
        if (!data.containsKey(categoryID)) {
            return null;
        }
        return data.get(categoryID).keySet();
    }

    public String[] getIDsForCategoryArray(String categoryID) {
        Set<String> idSet = getIDsForCategory(categoryID);
        if (idSet == null) {
            return new String[0];
        }
        return idSet.toArray(new String[0]);
    }

    public Data getData(String categoryID, String objectID) {
        if (data.get(categoryID) == null) {
            return null;
        }
        return data.get(categoryID).get(objectID);
    }

    public Map<String, Map<String, Data>> getAllData() {
        return data;
    }

    public void clearData() {
        data.clear();
    }

    public void saveObjectData(Data objectData, Data initialData) {
        if (!(objectData instanceof DataObject objectDataCast)) {
            throw new IllegalArgumentException("Top-level saved data must be an object");
        }
        String objectID = objectDataCast.getID();
        if (objectID == null) {
            throw new IllegalArgumentException("Top-level object must have an ID");
        }
        String categoryID = objectDataCast.getTemplate().id();
        if (initialData != null) {
            String initialID = ((DataObject) initialData).getID();
            String newID = objectDataCast.getID();
            if (!initialID.equals(newID)) { // Edit with new ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).remove(initialID);
                data.get(categoryID).put(objectID, objectData);
                if (objectDataCast.getTemplate().topLevel()) {
                    main.getBrowserFrame().removeGameObject(categoryID, initialID);
                    main.getBrowserFrame().addGameObject(categoryID, objectID, true);
                }
                renameReferences(categoryID, initialID, objectID);
            } else { // Edit with same ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).put(objectID, objectData);
                if (objectDataCast.getTemplate().topLevel()) {
                    main.getBrowserFrame().updateCategory(categoryID);
                }
            }
        } else { // New object instance
            if (!data.containsKey(categoryID)) {
                data.put(categoryID, new HashMap<>());
            }
            data.get(categoryID).put(objectID, objectData);
            if (objectDataCast.getTemplate().topLevel()) {
                main.getBrowserFrame().addGameObject(categoryID, objectID, true);
            }
        }
    }

    public void newObject(String categoryID) {
        main.getEditorManager().openEditorFrame(categoryID, null, main.getTemplate(categoryID), null, null);
    }

    public void newObject(String categoryID, DataSaveTarget saveTargetOverride) {
        main.getEditorManager().openEditorFrame(categoryID, null, main.getTemplate(categoryID), null, saveTargetOverride);
    }

    public void editObject(String categoryID, String objectID) {
        main.getEditorManager().openEditorFrame(categoryID, objectID, main.getTemplate(categoryID), getData(categoryID, objectID), null);
    }

    public void editObject(String categoryID, String objectID, DataSaveTarget saveTargetOverride) {
        main.getEditorManager().openEditorFrame(categoryID, objectID, main.getTemplate(categoryID), getData(categoryID, objectID), saveTargetOverride);
    }

    public String duplicateObject(String categoryID, String objectID) {
        Data objectData = getData(categoryID, objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        if (main.getTemplate(categoryID).topLevel()) {
            main.getBrowserFrame().addGameObject(categoryID, newObjectID, false);
            main.getBrowserFrame().setSelectedNode(categoryID, objectID);
        }
        return newObjectID;
    }

    public boolean deleteObject(String categoryID, String objectID) {
        int objectReferenceCount = findReferences(categoryID, objectID).size();
        int confirmResult;
        if (objectReferenceCount > 0) {
            Object[] confirmOptions = {"Delete", "View References", "Cancel"};
            confirmResult = JOptionPane.showOptionDialog(main.getBrowserFrame(), "Are you sure you want to delete " + objectID + "?\nReferences: " + objectReferenceCount, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        } else {
            Object[] confirmOptions = {"Delete", "Cancel"};
            confirmResult = JOptionPane.showOptionDialog(main.getBrowserFrame(), "Are you sure you want to delete " + objectID + "?\nReferences: " + 0, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        }
        if (confirmResult == 0) {
            data.get(categoryID).remove(objectID);
            main.getEditorManager().closeEditorFrameIfActive(categoryID, objectID);
            if (main.getTemplate(categoryID).topLevel()) {
                main.getBrowserFrame().removeGameObject(categoryID, objectID);
            }
            return true;
        } else if (confirmResult == 1 && objectReferenceCount > 0) {
            displayReferences(categoryID, objectID);
        }
        return false;
    }

    public void displayReferences(String referenceCategoryID, String referenceObjectID) {
        Set<Reference> references = findReferences(referenceCategoryID, referenceObjectID);
        main.getReferenceListManager().openReferenceList(references);
    }

    public void renameReferences(String referenceCategoryID, String referenceObjectID, String newObjectID) {
        renameReferencesInData(main.getConfigMenuManager().getConfigData(), referenceCategoryID, referenceObjectID, newObjectID);
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                Data currentObject = data.get(category).get(object);
                renameReferencesInData(currentObject, referenceCategoryID, referenceObjectID, newObjectID);
            }
        }
    }

    public Map<String, Map<String, Data>> getAllDataCopy() {
        Map<String, Map<String, Data>> dataCopy = new HashMap<>();
        for (Map.Entry<String, Map<String, Data>> categoryEntry : data.entrySet()) {
            Map<String, Data> categoryDataCopy = new HashMap<>();
            for (Map.Entry<String, Data> objectEntry : categoryEntry.getValue().entrySet()) {
                categoryDataCopy.put(objectEntry.getKey(), objectEntry.getValue() == null ? null : objectEntry.getValue().createCopy());
            }
            dataCopy.put(categoryEntry.getKey(), categoryDataCopy);
        }
        return dataCopy;
    }

    public boolean hasChangesFrom(Map<String, Map<String, Data>> comparisonData) {
        return !Objects.equals(data, comparisonData);
    }

    private Set<Reference> findReferences(String referenceCategoryID, String referenceObjectID) {
        Set<Reference> references = new HashSet<>();
        if (dataContainsReference(main.getConfigMenuManager().getConfigData(), referenceCategoryID, referenceObjectID)) {
            references.add(new Reference("", "config"));
        }
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                Data currentObject = data.get(category).get(object);
                if (dataContainsReference(currentObject, referenceCategoryID, referenceObjectID)) {
                    references.add(new Reference(category, object));
                }
            }
        }
        return references;
    }

    private String generateDuplicateObjectID(String categoryID, String objectID) {
        Set<String> existingIDs = getIDsForCategory(categoryID);
        String baseCopyID = objectID + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

    private boolean dataContainsReference(Data data, String categoryID, String objectID) {
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

    private void renameReferencesInData(Data data, String categoryID, String objectID, String newObjectID) {
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
