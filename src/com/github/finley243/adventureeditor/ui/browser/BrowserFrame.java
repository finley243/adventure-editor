package com.github.finley243.adventureeditor.ui.browser;

import com.github.finley243.adventureeditor.DataManager;
import com.github.finley243.adventureeditor.EditorManager;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserCategoryNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserNode;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserObjectNode;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Map;

public class BrowserFrame extends JDialog implements ObjectUpdateListener, CategoryUpdateListener, ProjectLoadListener {

    private final EditorManager editorManager;
    private final DataManager dataManager;
    private final BrowserTree browserTree;
    private final DataSaveTarget topLevelSaveTarget;
    private final ParameterFactory parameterFactory;

    public BrowserFrame(Window mainFrame, EditorManager editorManager, DataManager dataManager, DataSaveTarget topLevelSaveTarget, ParameterFactory parameterFactory) {
        super(mainFrame);
        this.editorManager = editorManager;
        this.dataManager = dataManager;
        this.topLevelSaveTarget = topLevelSaveTarget;
        this.parameterFactory = parameterFactory;

        this.setTitle("Browser");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.browserTree = new BrowserTree(this);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());

        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setPreferredSize(new Dimension(350, 500));
        browserPanel.add(browserScrollPane, BorderLayout.CENTER);
        this.getContentPane().add(browserPanel);

        this.pack();
        this.setVisible(true);
        //this.setLocationRelativeTo(null);
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = ((JFrame) getParent()).getContentPane().getLocationOnScreen().x;
        int windowY = ((JFrame) getParent()).getContentPane().getLocationOnScreen().y;
        int windowWidth = getWidth();
        int windowHeight = ((JFrame) getParent()).getContentPane().getHeight();
        this.setSize(windowWidth, windowHeight);
        this.setLocation(windowX, windowY);
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

    public void reloadBrowserData(Map<String, Template> templates, Map<String, Map<String, Data>> data) {
        editorManager.closeAllActiveEditorFrames();
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

    public void openContextMenu(BrowserNode node, int posX, int posY) {
        JPopupMenu contextMenu = node.getContextMenu(this);
        if (contextMenu != null) {
            contextMenu.show(this, posX, posY);
        }
    }

    public void openReferenceList(BrowserObjectNode node) {
        dataManager.displayReferences(node.getCategoryID(), node.getObjectID(), this, parameterFactory);
    }

    public void newObject(BrowserNode node) {
        if (node instanceof BrowserCategoryNode categoryNode) {
            dataManager.newObject(categoryNode.getCategoryID(), topLevelSaveTarget, this, parameterFactory);
        } else if (node instanceof BrowserObjectNode objectNode) {
            dataManager.newObject(objectNode.getCategoryID(), topLevelSaveTarget, this, parameterFactory);
        }
    }

    public void editObject(BrowserObjectNode node) {
        dataManager.editObject(node.getCategoryID(), node.getObjectID(), topLevelSaveTarget, this, parameterFactory);
    }

    public void duplicateObject(BrowserObjectNode node) {
        dataManager.duplicateObject(node.getCategoryID(), node.getObjectID());
    }

    public void deleteObject(BrowserObjectNode node) {
        dataManager.deleteObject(node.getCategoryID(), node.getObjectID(), this, parameterFactory);
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

    @Override
    public void onCreateNewObject(String categoryID, String objectID) {
        addGameObject(categoryID, objectID, true);
    }

    @Override
    public void onObjectIDChange(String categoryID, String objectIDPrevious, String objectIDNew) {
        removeGameObject(categoryID, objectIDPrevious);
        addGameObject(categoryID, objectIDNew, true);
    }

    @Override
    public void onDuplicateObject(String categoryID, String objectIDOriginal, String objectIDNew) {
        addGameObject(categoryID, objectIDNew, false);
        setSelectedNode(categoryID, objectIDOriginal);
    }

    @Override
    public void onDeleteObject(String categoryID, String objectID) {
        removeGameObject(categoryID, objectID);
    }

    @Override
    public void onCategoryUpdate(String categoryID) {
        browserTree.updateCategory(categoryID);
    }

    @Override
    public void onLoadProject(Map<String, Template> templates, Map<String, Map<String, Data>> loadedData) {
        reloadBrowserData(templates, loadedData);
    }

}
