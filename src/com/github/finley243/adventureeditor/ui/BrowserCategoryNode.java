package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BrowserCategoryNode extends DefaultMutableTreeNode {

    private final Main main;
    private final String categoryID;
    private final String name;
    private final Map<String, BrowserObjectNode> objectNodes;

    public BrowserCategoryNode(Main main, String categoryID, String name) {
        super(name);
        this.main = main;
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
        BrowserObjectNode objectNode = new BrowserObjectNode(main, objectID, categoryID);
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

    public void openContextMenu(Component component, Point point) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuNew = new JMenuItem("New " + name);
        menuNew.addActionListener(e -> main.getDataManager().newObject(categoryID));
        menu.add(menuNew);
        menu.show(component, point.x, point.y);
    }

}
