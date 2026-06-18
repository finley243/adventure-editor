package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

public class TopLevelSaveTarget implements DataSaveTarget {

    private final DataManager dataManager;
    private final EditorManager editorManager;

    public TopLevelSaveTarget(DataManager dataManager, EditorManager editorManager) {
        this.dataManager = dataManager;
        this.editorManager = editorManager;
    }

    @Override
    public void saveObjectData(String editorID, Data data, Data initialData) {
        dataManager.saveObjectData(data, initialData);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        editorManager.closeEditorFrameIfActive(frame.getTemplate().id(), frame.getObjectID());
    }

    @Override
    public ErrorData checkForSaveDataErrors(Data currentData, Data initialData) {
        boolean isNewInstance = initialData == null;
        String categoryID = ((DataObject) currentData).getTemplate().id();
        String currentID = ((DataObject) currentData).getID();
        if (currentID == null || currentID.trim().isEmpty()) {
            return new ErrorData(true, "ID cannot be empty.");
        }
        if (isNewInstance) {
            if (dataManager.categoryContainsID(categoryID, currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        } else {
            String initialID = ((DataObject) initialData).getID();
            if (!initialID.equals(currentID) && dataManager.categoryContainsID(categoryID, currentID)) {
                return new ErrorData(true, "An object with ID \"" + currentID + "\" already exists.");
            }
        }
        return new ErrorData(false, null);
    }

}
