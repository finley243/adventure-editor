package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

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

    public void openContextMenu(Component component, Point point) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuNew = new JMenuItem("New " + main.getTemplate(categoryID).name());
        menuNew.addActionListener(e -> main.openEditorMenu(main.getTemplate(categoryID), null));
        menu.add(menuNew);
        JMenuItem menuOpen = new JMenuItem("Open " + objectID);
        menuOpen.addActionListener(e -> openInEditor());
        menu.add(menuOpen);
        JMenuItem menuDuplicate = new JMenuItem("Duplicate " + objectID);
        menuDuplicate.addActionListener(e -> System.out.println("DUPLICATE: " + objectID));
        menu.add(menuDuplicate);
        JMenuItem menuDelete = new JMenuItem("Delete " + objectID);
        menuDelete.addActionListener(e -> System.out.println("DELETE: " + objectID));
        menu.add(menuDelete);
        menu.show(component, point.x, point.y);
    }

}
