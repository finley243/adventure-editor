package com.github.finley243.adventureeditor.ui;

import javax.swing.tree.DefaultMutableTreeNode;

public class BrowserObjectNode extends DefaultMutableTreeNode {

    private final String objectID;
    private final String categoryID;

    public BrowserObjectNode(String objectID, String categoryID) {
        super(objectID);
        this.objectID = objectID;
        this.categoryID = categoryID;
    }

    public String getObjectID() {
        return objectID;
    }

    public String getCategoryID() {
        return categoryID;
    }

}
