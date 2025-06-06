package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

public interface DataSaveTarget {

    void saveObjectData(String editorID, Data data, Data initialData);

    void onEditorFrameClose(EditorFrame frame);

    ErrorData isDataValidOrShowDialog(Data currentData, Data initialData);

    record ErrorData(boolean hasError, String message) {}

}
