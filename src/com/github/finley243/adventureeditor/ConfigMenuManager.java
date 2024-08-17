package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

import java.util.Objects;

public class ConfigMenuManager implements DataSaveTarget {

    public static final String CONFIG_TEMPLATE = "config";
    private static final String PROJECT_NAME_KEY = "gameName";

    private final Main main;

    private Data configData;
    private EditorFrame configFrame;

    public ConfigMenuManager(Main main) {
        this.main = main;
    }

    public void openConfigMenu() {
        if (!main.getProjectManager().isProjectLoaded()) {
            return;
        }
        if (configFrame != null) {
            configFrame.toFront();
            configFrame.requestFocus();
        } else {
            configFrame = new EditorFrame(main, null, main.getBrowserFrame(), main.getTemplate(CONFIG_TEMPLATE), configData, this, true);
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
    }

    public Data getConfigData() {
        return configData;
    }

    public void clearConfigData() {
        configData = null;
    }

    public boolean hasChangesFrom(Data otherData) {
        return !Objects.equals(configData, otherData);
    }

    @Override
    public void saveObjectData(String editorID, Data data, Data initialData) {
        configData = data;
        main.getProjectManager().updateProjectName();
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        configFrame = null;
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        String currentProjectName = ((DataString) ((DataObject) currentData).getValue().get(PROJECT_NAME_KEY)).getValue();
        if (currentProjectName == null || currentProjectName.trim().isEmpty()) {
            return new ErrorData(true, "Game name cannot be empty.");
        }
        return new ErrorData(false, null);
    }

}
