package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

public class Main {

    private static final String TEMPLATE_DIRECTORY = "src/templates";
    private static final String DATA_DIRECTORY_TEST = "src/gamefiles";

    private final Map<String, Template> templates;
    private final Map<String, Map<String, Data>> data;

    private final BrowserTree browserTree;

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Main main = new Main();
    }

    public Main() throws ParserConfigurationException, IOException, SAXException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.templates = DataLoader.loadTemplates(new File(TEMPLATE_DIRECTORY));
        this.data = DataLoader.loadFromDir(new File(DATA_DIRECTORY_TEST), templates);
        this.browserTree = new BrowserTree(this);
        EventQueue.invokeLater(this::run);
    }

    public void run() {
        JFrame frame = new JFrame("AdventureEditor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setLayout(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        //JSplitPane splitPane = new JSplitPane();
        //frame.getContentPane().add(splitPane);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.showOpenDialog(frame);
        });
        JMenuItem fileSave = new JMenuItem("Save");
        fileMenu.add(fileOpen);
        fileMenu.add(fileSave);

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());

        loadBrowser();

        browserTree.setPreferredSize(new Dimension(400, 400));
        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setPreferredSize(new Dimension(400, 800));
        browserPanel.add(browserScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(browserPanel);
        //splitPane.setLeftComponent(browserPanel);

        //JPanel editorPanel = new JPanel();
        //splitPane.setRightComponent(editorPanel);
        //editorPanel.setLayout(new BorderLayout());

        frame.setVisible(true);

        //EditorElement roomElement = buildMenuForTemplate(templates.get("room"));
        //editorPanel.add(roomElement);
        //roomElement.setData(data.get("room").get("wilsons_corner_store"));

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public Template getTemplate(String categoryID) {
        return templates.get(categoryID);
    }

    public Set<String> getIDsForCategory(String categoryID) {
        if (!data.containsKey(categoryID)) {
            return null;
        }
        return data.get(categoryID).keySet();
    }

    public Data getData(String categoryID, String object) {
        if (data.get(categoryID) == null) {
            return null;
        }
        return data.get(categoryID).get(object);
    }

    private void loadBrowser() {
        for (String category : templates.keySet()) {
            if (templates.get(category).topLevel()) {
                browserTree.addCategory(category, templates.get(category).name());
            }
        }
        for (String category : data.keySet()) {
            for (String object : data.get(category).keySet()) {
                browserTree.addGameObject(category, object);
            }
        }
    }

    public void newObject(String categoryID) {
        Template template = templates.get(categoryID);
        EditorFrame editorFrame = new EditorFrame(this, template, null, true);
    }

    public void editObject(String categoryID, String objectID) {
        Template template = templates.get(categoryID);
        Data objectData = data.get(categoryID).get(objectID);
        EditorFrame editorFrame = new EditorFrame(this, template, objectData, true);
    }

    public void duplicateObject(String categoryID, String objectID) {
        Data objectData = data.get(categoryID).get(objectID);
        Data objectDataCopy = objectData.createCopy();
        String newObjectID = generateDuplicateObjectID(categoryID, objectID);
        if (objectDataCopy instanceof DataObject dataObject) {
            dataObject.replaceID(newObjectID);
        }
        data.get(categoryID).put(newObjectID, objectDataCopy);
        browserTree.addGameObject(categoryID, newObjectID);
    }

    public void deleteObject(String categoryID, String objectID) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(browserTree, "Are you sure you want to delete " + objectID + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            data.get(categoryID).remove(objectID);
            browserTree.removeGameObject(categoryID, objectID);
        }
    }

    private String generateDuplicateObjectID(String categoryID, String objectID) {
        Set<String> existingIDs = getIDsForCategory(categoryID);
        String baseCopyID = objectID + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

}