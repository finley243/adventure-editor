package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.*;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DataManager {

    private final EditorManager editorManager;
    private final TemplateRegistry templateRegistry;
    private final ConfigMenuManager configMenuManager;

    private ReferenceListManager referenceListManager;

    private Map<String, Map<String, Data>> data;

    private final List<CategoryUpdateListener> categoryUpdateListeners;
    private final List<ObjectUpdateListener> objectUpdateListeners;

    public DataManager(EditorManager editorManager, TemplateRegistry templateRegistry, ConfigMenuManager configMenuManager) {
        this.editorManager = editorManager;
        this.templateRegistry = templateRegistry;
        this.configMenuManager = configMenuManager;
        this.data = null;
        this.categoryUpdateListeners = new ArrayList<>();
        this.objectUpdateListeners = new ArrayList<>();
    }

    public void resolveReferenceListManager(ReferenceListManager referenceListManager) {
        if (this.referenceListManager != null) throw new IllegalStateException("ReferenceListManager has already been resolved");
        this.referenceListManager = referenceListManager;
    }

    private ReferenceListManager getReferenceListManager() {
        if (this.referenceListManager == null) throw new IllegalStateException("ReferenceListManager has not been resolved");
        return referenceListManager;
    }

    public void registerCategoryUpdateListener(CategoryUpdateListener categoryUpdateListener) {
        this.categoryUpdateListeners.add(categoryUpdateListener);
    }

    public void registerObjectUpdateListener(ObjectUpdateListener objectUpdateListener) {
        this.objectUpdateListeners.add(objectUpdateListener);
    }

    public void setData(Map<String, Map<String, Data>> data) {
        this.data = new HashMap<>(data);
    }

    public boolean categoryContainsID(String categoryID, String objectID) {
        if (!data.containsKey(categoryID)) return false;
        return data.get(categoryID).containsKey(objectID);
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
        data = null;
    }

    public void addEmptyData() {
        data = new HashMap<>();
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

    public void newObject(String categoryID, DataSaveTarget saveTarget, Window parentWindow, ParameterFactory parameterFactory) {
        editorManager.openEditorFrame(categoryID, null, templateRegistry.getTemplate(categoryID), null, saveTarget, parentWindow, parameterFactory);
    }

    public void editObject(String categoryID, String objectID, DataSaveTarget saveTarget, Window parentWindow, ParameterFactory parameterFactory) {
        editorManager.openEditorFrame(categoryID, objectID, templateRegistry.getTemplate(categoryID), getData(categoryID, objectID), saveTarget, parentWindow, parameterFactory);
    }

    public String duplicateObject(String categoryID, String objectID) {
        Data objectData = getData(categoryID, objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        if (templateRegistry.getTemplate(categoryID).topLevel()) {
            onObjectDuplication(categoryID, objectID, newObjectID);
        }
        return newObjectID;
    }

    public boolean deleteObject(String categoryID, String objectID, Window parentWindow, ParameterFactory parameterFactory) {
        int referenceCount = findReferences(categoryID, objectID).size();
        DeleteObjectConfirmationResult result = UIUtils.deleteObjectConfirmation(objectID, referenceCount, parentWindow);
        if (result == DeleteObjectConfirmationResult.DELETE) {
            data.get(categoryID).remove(objectID);
            editorManager.closeEditorFrameIfActive(categoryID, objectID);
            if (templateRegistry.getTemplate(categoryID).topLevel()) {
                onObjectDelete(categoryID, objectID);
            }
            return true;
        } else if (result == DeleteObjectConfirmationResult.VIEW_REFERENCES) {
            displayReferences(categoryID, objectID, parentWindow, parameterFactory);
        }
        return false;
    }

    public void displayReferences(String referenceCategoryID, String referenceObjectID, Window parentWindow, ParameterFactory parameterFactory) {
        Set<Reference> references = findReferences(referenceCategoryID, referenceObjectID);
        referenceListManager.openReferenceList(references, parentWindow, parameterFactory);
    }

    public void renameReferences(String referenceCategoryID, String referenceObjectID, String newObjectID) {
        renameReferencesInData(configMenuManager.getConfigData(), referenceCategoryID, referenceObjectID, newObjectID);
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
        if (dataContainsReference(configMenuManager.getConfigData(), referenceCategoryID, referenceObjectID)) {
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

    private void onCategoryUpdate(String categoryID) {
        for (CategoryUpdateListener listener : categoryUpdateListeners) {
            listener.onCategoryUpdate(categoryID);
        }
    }

    private void onObjectCreation(String categoryID, String objectID) {
        for (ObjectUpdateListener listener : objectUpdateListeners) {
            listener.onCreateNewObject(categoryID, objectID);
        }
    }

    private void onObjectIDChange(String categoryID, String objectIDPrevious, String objectIDNew) {
        for (ObjectUpdateListener listener : objectUpdateListeners) {
            listener.onObjectIDChange(categoryID, objectIDPrevious, objectIDNew);
        }
    }

    private void onObjectDuplication(String categoryID, String objectIDOriginal, String objectIDNew) {
        for (ObjectUpdateListener listener : objectUpdateListeners) {
            listener.onDuplicateObject(categoryID, objectIDOriginal, objectIDNew);
        }
    }

    private void onObjectDelete(String categoryID, String objectID) {
        for (ObjectUpdateListener listener : objectUpdateListeners) {
            listener.onDeleteObject(categoryID, objectID);
        }
    }

}
