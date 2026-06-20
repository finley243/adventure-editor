package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.ProjectNameChangeListener;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigMenuManager implements DataSaveTarget {

    private static final String PROJECT_NAME_KEY = "gameName";

    private final Template configTemplate;

    private Data configData;
    private EditorFrame configFrame;

    private final List<ProjectNameChangeListener> projectNameChangeListeners;

    public ConfigMenuManager(Template configTemplate) {
        this.configTemplate = configTemplate;
        this.projectNameChangeListeners = new ArrayList<>();
    }

    public void registerProjectNameChangeListener(ProjectNameChangeListener listener) {
        projectNameChangeListeners.add(listener);
    }

    public void openConfigMenu(Window parentWindow, ParameterFactory parameterFactory) {
        if (configData == null) {
            return;
        }
        if (configFrame != null) {
            configFrame.toFront();
            configFrame.requestFocus();
        } else {
            configFrame = new EditorFrame(parentWindow, configTemplate, configData, true, parameterFactory);
        }
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

    public void setConfigData(Data data) {
        configData = data;
        onProjectNameChange(getProjectName());
    }

    public Data getConfigData() {
        if (configData == null) return new DataObject(configTemplate, new HashMap<>());
        return configData;
    }

    public void clearConfigData() {
        if (configFrame != null) {
            configFrame.dispose();
            configFrame = null;
        }
        configData = new DataObject(configTemplate, new HashMap<>());
        onProjectNameChange(null);
    }

    public boolean hasChangesFrom(Data otherData) {
        return !Objects.equals(configData, otherData);
    }

    @Override
    public void saveObjectData(String editorID, Data data, Data initialData) {
        configData = data;
        onProjectNameChange(getProjectName());
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        configFrame = null;
    }

    @Override
    public ErrorData checkForSaveDataErrors(Data currentData, Data initialData) {
        String currentProjectName = ((DataString) ((DataObject) currentData).getValue().get(PROJECT_NAME_KEY)).getValue();
        if (currentProjectName == null || currentProjectName.trim().isEmpty()) {
            return new ErrorData(true, "Game name cannot be empty.");
        }
        return new ErrorData(false, null);
    }

    public void onProjectNameChange(String name) {
        for (ProjectNameChangeListener listener : projectNameChangeListeners) {
            listener.onProjectNameChange(name);
        }
    }

}
