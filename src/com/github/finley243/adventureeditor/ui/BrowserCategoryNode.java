package com.github.finley243.adventureeditor.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;

public class BrowserCategoryNode extends DefaultMutableTreeNode {

    private final String categoryID;
    private final String name;

    public BrowserCategoryNode(String categoryID, String name) {
        super(name);
        this.categoryID = categoryID;
        this.name = name;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getName() {
        return name;
    }

    public void addGameObject(String objectID) {
        this.add(new BrowserObjectNode(objectID, categoryID));
        this.children.sort(Comparator.comparing(o -> ((BrowserObjectNode) o).getObjectID()));
    }

}
