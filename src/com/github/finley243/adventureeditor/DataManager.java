package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.ReferenceUtils;

import java.util.*;

public class DataManager {

    private Map<String, Map<String, Data>> data;
    private Map<String, Map<String, Data>> lastSavedData;

    public DataManager() {
        this.data = new HashMap<>();
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

    public Map<String, Set<String>> getAllObjectIDs() {
        Map<String, Set<String>> objectIDs = new HashMap<>();
        for (Map.Entry<String, Map<String, Data>> entry : data.entrySet()) {
            objectIDs.put(entry.getKey(), entry.getValue().keySet());
        }
        return objectIDs;
    }

    public void clearData() {
        data = new HashMap<>();
    }

    public void removeData(String categoryID, String objectID) {
        data.get(categoryID).remove(objectID);
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
