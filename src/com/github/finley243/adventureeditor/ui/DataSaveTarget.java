package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

import java.awt.*;

public interface DataSaveTarget {

    void saveObjectData(Data data, Data initialData);

    void onEditorFrameClose(EditorFrame frame);

    boolean isDataValidOrShowDialog(Component parentComponent, Data currentData, Data initialData);

}
