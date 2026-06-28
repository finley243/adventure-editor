package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.ReferenceUtils;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import java.util.HashMap;
import java.util.Objects;

public class ConfigMenuManager {

    private static final String PROJECT_NAME_KEY = "gameName";

    private final Template configTemplate;

    private Data configData;
    private Data lastSavedConfigData;
    private EditorFrame configFrame;

    public ConfigMenuManager(Template configTemplate) {
        this.configTemplate = configTemplate;
    }

    public String getProjectName() {
        if (configData == null) {
            return null;
        }
        DataObject configDataObject = (DataObject) configData;
        Data projectNameData = configDataObject.getValue().get(PROJECT_NAME_KEY);
        if (!(projectNameData instanceof DataString projectNameDataString)) {
            return null;
        }
        return projectNameDataString.getValue();
    }

    public void loadConfigData(Data data) {
        configData = data;
        setSavedChanges();
    }

    public void setConfigData(Data data) {
        configData = data;
    }

    public Data getConfigData() {
        if (configData == null) return new DataObject(configTemplate, new HashMap<>());
        return configData;
    }

    public void unloadConfigData() {
        if (configFrame != null) {
            configFrame.dispose();
            configFrame = null;
        }
        configData = new DataObject(configTemplate, new HashMap<>());
        setSavedChanges();
    }

    public boolean hasUnsavedChanges() {
        return !Objects.equals(configData, lastSavedConfigData);
    }

    public void setSavedChanges() {
        this.lastSavedConfigData = configData == null ? null : configData.createCopy();
    }

    public String getProjectNameFromData(Data data) {
        return ((DataString) ((DataObject) data).getValue().get(PROJECT_NAME_KEY)).getValue();
    }

    public Reference findReference(String referenceCategoryID, String referenceObjectID) {
        if (ReferenceUtils.dataContainsReference(configData, referenceCategoryID, referenceObjectID)) {
            return new Reference("", "config");
        }
        return null;
    }

    public void renameReferences(String referenceCategoryID, String referenceObjectID, String newObjectID) {
        ReferenceUtils.renameReferencesInData(configData, referenceCategoryID, referenceObjectID, newObjectID);
        ReferenceUtils.renameReferencesInData(lastSavedConfigData, referenceCategoryID, referenceObjectID, newObjectID);
    }

}
