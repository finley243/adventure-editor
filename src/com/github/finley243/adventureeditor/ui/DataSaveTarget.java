package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

public interface DataSaveTarget {

    void saveObjectData(Data data, Data initialData);

    void onEditorFrameClose(EditorFrame frame);

    ErrorData isDataValidOrShowDialog(Data currentData, Data initialData);

    public record ErrorData(boolean hasError, String message) {}

}
