package com.github.finley243.adventureeditor.ui.browser.node;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;

public class BrowserObjectNode extends BrowserNode {

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

    @Override
    public JPopupMenu getContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(e -> main.getDataManager().editObject(categoryID, objectID));
        menu.add(menuOpen);
        /*JMenuItem menuNew = new JMenuItem("New " + main.getTemplate(categoryID).name());
        menuNew.addActionListener(e -> main.newObject(categoryID));
        menu.add(menuNew);*/
        JMenuItem menuDuplicate = new JMenuItem("Duplicate");
        menuDuplicate.addActionListener(e -> main.getDataManager().duplicateObject(categoryID, objectID));
        menu.add(menuDuplicate);
        JMenuItem menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(e -> main.getDataManager().deleteObject(categoryID, objectID));
        menu.add(menuDelete);
        JMenuItem menuReferences = new JMenuItem("Find references");
        menuReferences.addActionListener(e -> main.getDataManager().displayReferences(categoryID, objectID));
        menu.add(menuReferences);
        return menu;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BrowserObjectNode objectNode && objectNode.objectID.equals(objectID) && objectNode.categoryID.equals(categoryID);
    }

    @Override
    public int hashCode() {
        return objectID.hashCode() + (31 * categoryID.hashCode());
    }

}
