package com.github.finley243.adventureeditor.ui;

public interface ObjectUpdateListener {

    void onCreateNewObject(String categoryID, String objectID);

    void onObjectIDChange(String categoryID, String objectIDPrevious, String objectIDNew);

}
