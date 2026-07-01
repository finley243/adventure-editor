package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;

public interface EditorSession {

    String getCurrentKey();

    void setCurrentKey(String key);

    Data getCurrentData();

    void setCurrentData(Data data);

    void refreshData(String newKey, Data newData);

}
