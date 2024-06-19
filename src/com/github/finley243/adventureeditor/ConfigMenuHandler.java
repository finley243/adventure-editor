package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;

public class ConfigMenuHandler implements DataSaveTarget {

    public static final String CONFIG_TEMPLATE = "config";

    private final Main main;

    private Data configData;
    private EditorFrame configFrame;

    public ConfigMenuHandler(Main main) {
        this.main = main;
    }

    public void openConfigMenu() {
        if (configFrame != null) {
            configFrame.toFront();
            configFrame.requestFocus();
        } else {
            configFrame = new EditorFrame(main, main.getTemplate(CONFIG_TEMPLATE), configData, this);
        }
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

    @Override
    public void saveObjectData(Data data, Data initialData) {
        configData = data;
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        configFrame = null;
    }

}
