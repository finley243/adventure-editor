package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class BrowserTree extends JTree {

    public BrowserTree() {
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Game Data");
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot, false);
        this.setModel(treeModel);

        treeRoot.add(new BrowserCategoryNode("actions"));
        treeRoot.add(new BrowserCategoryNode("actors"));
        treeRoot.add(new BrowserCategoryNode("attackTypes"));
        treeRoot.add(new BrowserCategoryNode("attributes"));
        treeRoot.add(new BrowserCategoryNode("damageTypes"));
        treeRoot.add(new BrowserCategoryNode("effects"));
        treeRoot.add(new BrowserCategoryNode("factions"));
        treeRoot.add(new BrowserCategoryNode("items"));
        treeRoot.add(new BrowserCategoryNode("linkTypes"));
        treeRoot.add(new BrowserCategoryNode("lootTables"));
        treeRoot.add(new BrowserCategoryNode("networks"));
        treeRoot.add(new BrowserCategoryNode("objects"));
        treeRoot.add(new BrowserCategoryNode("phrases"));
        treeRoot.add(new BrowserCategoryNode("rooms"));
        treeRoot.add(new BrowserCategoryNode("scenes"));
        treeRoot.add(new BrowserCategoryNode("scripts"));
        treeRoot.add(new BrowserCategoryNode("senseTypes"));
        treeRoot.add(new BrowserCategoryNode("skills"));
        treeRoot.add(new BrowserCategoryNode("weaponClasses"));

        this.expandRow(0);
    }

}
