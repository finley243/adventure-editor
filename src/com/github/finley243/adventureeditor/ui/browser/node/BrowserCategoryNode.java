package com.github.finley243.adventureeditor.ui.browser.node;

import com.github.finley243.adventureeditor.PresenterActions;

import javax.swing.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BrowserCategoryNode extends BrowserNode {

    private final String categoryID;
    private final String name;
    private final Map<String, BrowserObjectNode> objectNodes;

    public BrowserCategoryNode(String categoryID, String name) {
        super(name);
        this.categoryID = categoryID;
        this.name = name;
        this.objectNodes = new HashMap<>();
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getName() {
        return name;
    }

    public void addGameObject(String objectID) {
        BrowserObjectNode objectNode = new BrowserObjectNode(objectID, categoryID);
        objectNodes.put(objectID, objectNode);
        this.add(objectNode);
        this.children.sort(Comparator.comparing(o -> ((BrowserObjectNode) o).getObjectID()));
    }

    public void removeGameObject(String objectID) {
        objectNodes.remove(objectID);
    }

    public BrowserObjectNode getObjectNode(String objectID) {
        return objectNodes.get(objectID);
    }

    @Override
    public JPopupMenu getContextMenu(PresenterActions presenter) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuNew = new JMenuItem("New " + name);
        menuNew.addActionListener(e -> presenter.onCreateObject(getCategoryID()));
        menu.add(menuNew);
        return menu;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BrowserCategoryNode categoryNode && categoryNode.categoryID.equals(categoryID);
    }

    @Override
    public int hashCode() {
        return categoryID.hashCode();
    }

}
