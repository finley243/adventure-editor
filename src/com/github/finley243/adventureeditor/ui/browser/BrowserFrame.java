package com.github.finley243.adventureeditor.ui.browser;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Map;

public class BrowserFrame extends JDialog {

    private final Main main;
    private final BrowserTree browserTree;

    public BrowserFrame(Main main, Window mainFrame) {
        super(mainFrame);
        this.main = main;

        this.setSize(800, 600);
        this.setTitle("Browser");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.browserTree = new BrowserTree(main);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());

        browserTree.setPreferredSize(new Dimension(400, 400));
        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setPreferredSize(new Dimension(400, 800));
        browserPanel.add(browserScrollPane, BorderLayout.CENTER);
        this.getContentPane().add(browserPanel);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void addGameObject(String categoryID, String newObjectID, boolean selectedAfterLoading) {
        browserTree.addGameObject(categoryID, newObjectID, selectedAfterLoading);
    }

    public void removeGameObject(String categoryID, String objectID) {
        browserTree.removeGameObject(categoryID, objectID);
    }

    public void setSelectedNode(String categoryID, String objectID) {
        browserTree.setSelectedNode(categoryID, objectID);
    }

    public void updateCategory(String categoryID) {
        browserTree.updateCategory(categoryID);
    }

    public void reloadBrowserData(Map<String, Template> templates, Map<String, Map<String, Data>> data) {
        main.getMainFrame().closeAllActiveEditorFrames();
        browserTree.clearData();
        browserTree.expandRow(0);
        for (String category : templates.keySet()) {
            if (templates.get(category).topLevel()) {
                browserTree.addCategory(category, templates.get(category).name());
            }
        }
        for (String category : data.keySet()) {
            if (templates.get(category).topLevel()) {
                for (String object : data.get(category).keySet()) {
                    this.addGameObject(category, object, false);
                }
            }
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // TODO - Handle window hiding
            super.processWindowEvent(e);
        } else {
            super.processWindowEvent(e);
        }
    }

}
