package com.github.finley243.adventureeditor.ui.browser.node;

import com.github.finley243.adventureeditor.PresenterActions;

import javax.swing.*;

public class BrowserObjectNode extends BrowserNode {

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

    @Override
    public JPopupMenu getContextMenu(PresenterActions presenter) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(e -> presenter.onEditObject(getCategoryID(), getObjectID()));
        menu.add(menuOpen);
        /*JMenuItem menuNew = new JMenuItem("New " + main.getTemplate(categoryID).name());
        menuNew.addActionListener(e -> main.newObject(categoryID));
        menu.add(menuNew);*/
        JMenuItem menuDuplicate = new JMenuItem("Duplicate");
        menuDuplicate.addActionListener(e -> presenter.onDuplicateObject(getCategoryID(), getObjectID()));
        menu.add(menuDuplicate);
        JMenuItem menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(e -> presenter.onDeleteObject(getCategoryID(), getObjectID()));
        menu.add(menuDelete);
        JMenuItem menuReferences = new JMenuItem("Find references");
        menuReferences.addActionListener(e -> presenter.onShowReferences(getCategoryID(), getObjectID()));
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
