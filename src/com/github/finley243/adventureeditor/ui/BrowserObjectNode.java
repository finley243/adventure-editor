package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.tree.DefaultMutableTreeNode;

public class BrowserObjectNode extends DefaultMutableTreeNode {

    private final Main main;
    private final String objectID;
    private final String categoryID;

    public BrowserObjectNode(Main main, String objectID, String categoryID) {
        super(objectID);
        this.main = main;
        this.objectID = objectID;
        this.categoryID = categoryID;
    }

    public String getObjectID() {
        return objectID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void openInEditor() {
        main.openEditorMenu(main.getTemplate(categoryID), main.getData(categoryID, objectID));
    }

}
