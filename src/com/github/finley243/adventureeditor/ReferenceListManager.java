package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.frame.ReferenceListFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import java.awt.*;
import java.util.Set;

public class ReferenceListManager {

    private static final String CONFIG_OBJECT_NAME = "config";

    private final ConfigMenuManager configMenuManager;
    private final DataManager dataManager;
    private final DataSaveTarget topLevelSaveTarget;

    private ReferenceListFrame referenceListFrame;

    public ReferenceListManager(ConfigMenuManager configMenuManager, DataManager dataManager, DataSaveTarget topLevelSaveTarget) {
        this.configMenuManager = configMenuManager;
        this.dataManager = dataManager;
        this.topLevelSaveTarget = topLevelSaveTarget;
    }

    public void openReferenceList(Set<Reference> references, Window parentWindow, ParameterFactory parameterFactory) {
        if (references.isEmpty()) {
            return;
        }
        if (referenceListFrame != null) {
            referenceListFrame.toFront();
            referenceListFrame.requestFocus();
        } else {
            referenceListFrame = new ReferenceListFrame(parentWindow, parameterFactory);
        }
        referenceListFrame.loadReferences(references);
    }

    public void openReference(String categoryID, String objectID, Window parentWindow, ParameterFactory parameterFactory) {
        if (categoryID.isEmpty() && objectID.equals(CONFIG_OBJECT_NAME)) {
            configMenuManager.openConfigMenu(parentWindow, parameterFactory);
        } else {
            dataManager.editObject(categoryID, objectID, topLevelSaveTarget, parentWindow, parameterFactory);
        }
    }

    public boolean onCloseReferenceList() {
        referenceListFrame = null;
        return true;
    }

}
