package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFieldFactory;

import java.util.Objects;

public class ConfigMenuManager implements DataSaveTarget {

    private static final String PROJECT_NAME_KEY = "gameName";

    private final TemplateRegistry templateRegistry;
    private final ProjectManager projectManager;
    private final ParameterFieldFactory parameterFactory;
    private final MainFrame mainFrame;

    private Data configData;
    private EditorFrame configFrame;

    public ConfigMenuManager(TemplateRegistry templateRegistry, ProjectManager projectManager, ParameterFieldFactory parameterFactory, MainFrame mainFrame) {
        this.templateRegistry = templateRegistry;
        this.projectManager = projectManager;
        this.parameterFactory = parameterFactory;
        this.mainFrame = mainFrame;
    }

    public void openConfigMenu() {
        if (!projectManager.isProjectLoaded()) {
            return;
        }
        if (configFrame != null) {
            configFrame.toFront();
            configFrame.requestFocus();
        } else {
            configFrame = new EditorFrame(null, mainFrame, templateRegistry.getConfigTemplate(), configData, this, true, parameterFactory);
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
        projectManager.updateProjectName();
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

}
