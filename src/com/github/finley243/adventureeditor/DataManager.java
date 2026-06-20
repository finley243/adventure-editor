package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.DeleteObjectConfirmationResult;
import com.github.finley243.adventureeditor.ui.ReferenceUtils;
import com.github.finley243.adventureeditor.ui.UIUtils;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import java.awt.*;
import java.util.*;

public class DataManager {

    private final EditorManager editorManager;
    private final TemplateRegistry templateRegistry;

    private ReferenceListManager referenceListManager;

    private Map<String, Map<String, Data>> data;
    private Map<String, Map<String, Data>> lastSavedData;

    public DataManager(EditorManager editorManager, TemplateRegistry templateRegistry) {
        this.editorManager = editorManager;
        this.templateRegistry = templateRegistry;
        this.data = new HashMap<>();
    }

    public void resolveReferenceListManager(ReferenceListManager referenceListManager) {
        if (this.referenceListManager != null) throw new IllegalStateException("ReferenceListManager has already been resolved");
        this.referenceListManager = referenceListManager;
    }

    private ReferenceListManager getReferenceListManager() {
        if (this.referenceListManager == null) throw new IllegalStateException("ReferenceListManager has not been resolved");
        return referenceListManager;
    }

    public void loadData(Map<String, Map<String, Data>> data) {
        this.data = new HashMap<>(data);
    }

    public void setData(String categoryID, String objectID, Data objectData) {
        data.computeIfAbsent(categoryID, k -> new HashMap<>()).put(objectID, objectData);
    }

    public boolean categoryContainsID(String categoryID, String objectID) {
        if (!data.containsKey(categoryID)) return false;
        return data.get(categoryID).containsKey(objectID);
    }

    public Set<String> getIDsForCategory(String categoryID) {
        if (!data.containsKey(categoryID)) {
            return Set.of();
        }
        return data.get(categoryID).keySet();
    }

    public String[] getIDsForCategoryArray(String categoryID) {
        Set<String> idSet = getIDsForCategory(categoryID);
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
        data = new HashMap<>();
    }

    public void removeData(String categoryID, String objectID) {
        data.get(categoryID).remove(objectID);
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
                    onObjectIDChange(categoryID, initialID, objectID);
                }
                renameReferences(categoryID, initialID, objectID);
            } else { // Edit with same ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).put(objectID, objectData);
                if (objectDataCast.getTemplate().topLevel()) {
                    onCategoryUpdate(categoryID);
                }
            }
        } else { // New object instance
            if (!data.containsKey(categoryID)) {
                data.put(categoryID, new HashMap<>());
            }
            data.get(categoryID).put(objectID, objectData);
            if (objectDataCast.getTemplate().topLevel()) {
                onObjectCreation(categoryID, objectID);
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

    public boolean hasUnsavedChanges() {
        return !Objects.equals(data, lastSavedData);
    }

    public void setSavedChanges() {
        this.lastSavedData = getAllDataCopy();
    }

    public Set<Reference> findReferences(String referenceCategoryID, String referenceObjectID) {
        Set<Reference> references = new HashSet<>();
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                Data currentObject = data.get(category).get(object);
                if (ReferenceUtils.dataContainsReference(currentObject, referenceCategoryID, referenceObjectID)) {
                    references.add(new Reference(category, object));
                }
            }
        }
        return references;
    }

    public void renameReferences(String referenceCategoryID, String referenceObjectID, String newObjectID) {
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                Data currentObject = data.get(category).get(object);
                ReferenceUtils.renameReferencesInData(currentObject, referenceCategoryID, referenceObjectID, newObjectID);
            }
        }
    }

}
