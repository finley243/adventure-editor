package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.*;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

public class Main {

    private static final String TEMPLATE_DIRECTORY = "src/templates";
    private static final String DATA_DIRECTORY_TEST = "src/gamefiles";

    private final Map<String, Template> templates;
    private final Map<String, Map<String, Data>> data;

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Main main = new Main();
    }

    public Main() throws ParserConfigurationException, IOException, SAXException {
        this.templates = DataLoader.loadTemplates(new File(TEMPLATE_DIRECTORY));
        this.data = DataLoader.loadFromDir(new File(DATA_DIRECTORY_TEST), templates);
        EventQueue.invokeLater(this::run);
    }

    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

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
        BrowserTree browserTree = new BrowserTree();

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

        browserTree.setPreferredSize(new Dimension(400, 600));
        JScrollPane browserScrollPane = new JScrollPane(browserTree);
        browserScrollPane.setViewportView(browserTree);
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

    private EditorElement buildMenuForTemplate(Template template) {
        return new ParameterFieldObject(template.name(), template, templates, data);
    }

}