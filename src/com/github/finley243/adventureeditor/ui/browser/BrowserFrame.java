package com.github.finley243.adventureeditor.ui.browser;

import com.github.finley243.adventureeditor.PresenterActions;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.browser.node.BrowserNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Set;

public class BrowserFrame extends JDialog {

    //private final DataManager dataManager;
    private final BrowserTree browserTree;
    private final TemplateRegistry templateRegistry;
    private PresenterActions presenter;

    public BrowserFrame(Window mainFrame, TemplateRegistry templateRegistry) {
        super(mainFrame);
        this.templateRegistry = templateRegistry;

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

    public void registerPresenter(PresenterActions presenter) {
        if (this.presenter != null) throw new IllegalStateException("Presenter is already registered");
        this.presenter = presenter;
        browserTree.registerPresenter(presenter);
    }

    private PresenterActions getPresenter() {
        if (presenter == null) throw new IllegalStateException("Presenter has not been registered");
        return presenter;
    }

    public void addGameObject(String categoryID, String newObjectID) {
        browserTree.addGameObject(categoryID, newObjectID);
    }

    public void removeGameObject(String categoryID, String objectID) {
        browserTree.removeGameObject(categoryID, objectID);
    }

    public void setSelectedNode(String categoryID, String objectID) {
        browserTree.setSelectedNode(categoryID, objectID);
    }

    public void setCategories() {
        browserTree.clearData();
        browserTree.expandRow(0);
        for (Map.Entry<String, Template> entry : templateRegistry.getAllTemplates().entrySet()) {
            if (entry.getValue().topLevel()) {
                browserTree.addCategory(entry.getKey(), entry.getValue().name());
            }
        }
    }

    public void clearCategories() {
        browserTree.clearData();
        browserTree.expandRow(0);
    }

    public void reloadBrowserObjects(Map<String, Set<String>> objects) {
        setCategories();
        for (String category : objects.keySet()) {
            if (templateRegistry.getTemplate(category).topLevel()) {
                for (String object : objects.get(category)) {
                    this.addGameObject(category, object);
                }
            }
        }
    }

    public void openContextMenu(BrowserNode node, int posX, int posY) {
        JPopupMenu contextMenu = node.getContextMenu(getPresenter());
        if (contextMenu != null) {
            contextMenu.show(this, posX, posY);
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
