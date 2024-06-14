package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Comparator;

public class BrowserCategoryNode extends DefaultMutableTreeNode {

    private final Main main;
    private final String categoryID;
    private final String name;

    public BrowserCategoryNode(Main main, String categoryID, String name) {
        super(name);
        this.main = main;
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
        this.add(new BrowserObjectNode(main, objectID, categoryID));
        this.children.sort(Comparator.comparing(o -> ((BrowserObjectNode) o).getObjectID()));
    }

    public void openContextMenu(Component component, Point point) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuNew = new JMenuItem("New " + name);
        menuNew.addActionListener(e -> main.openEditorMenu(main.getTemplate(categoryID), null));
        menu.add(menuNew);
        menu.show(component, point.x, point.y);
    }

}
