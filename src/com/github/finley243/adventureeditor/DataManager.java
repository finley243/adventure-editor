package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
                main.getBrowserFrame().removeGameObject(categoryID, initialID);
                data.get(categoryID).put(objectID, objectData);
                main.getBrowserFrame().addGameObject(categoryID, objectID, true);
            } else { // Edit with same ID
                if (!data.containsKey(categoryID)) {
                    data.put(categoryID, new HashMap<>());
                }
                data.get(categoryID).put(objectID, objectData);
                main.getBrowserFrame().updateCategory(categoryID);
            }
        } else { // New object instance
            if (!data.containsKey(categoryID)) {
                data.put(categoryID, new HashMap<>());
            }
            data.get(categoryID).put(objectID, objectData);
            main.getBrowserFrame().addGameObject(categoryID, objectID, true);
        }
    }

    public void newObject(String categoryID) {
        main.getBrowserFrame().openEditorFrame(categoryID, null, main.getTemplate(categoryID), null);
    }

    public void editObject(String categoryID, String objectID) {
        main.getBrowserFrame().openEditorFrame(categoryID, objectID, main.getTemplate(categoryID), getData(categoryID, objectID));
    }

    public void duplicateObject(String categoryID, String objectID) {
        Data objectData = getData(categoryID, objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        main.getBrowserFrame().addGameObject(categoryID, newObjectID, false);
        main.getBrowserFrame().setSelectedNode(categoryID, objectID);
    }

    public void deleteObject(String categoryID, String objectID) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(main.getBrowserFrame(), "Are you sure you want to delete " + objectID + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            data.get(categoryID).remove(objectID);
            main.getBrowserFrame().closeEditorFrameIfActive(categoryID, objectID);
            main.getBrowserFrame().removeGameObject(categoryID, objectID);
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

    private String generateDuplicateObjectID(String categoryID, String objectID) {
        Set<String> existingIDs = getIDsForCategory(categoryID);
        String baseCopyID = objectID + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

}
