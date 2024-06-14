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
        /*for (String dataType : data.keySet()) {
            Map<String, Data> instances = data.get(dataType);
            System.out.println("Data Type: " + dataType);
            for (String instanceID : instances.keySet()) {
                Data instance = instances.get(instanceID);
                System.out.println(" - Instance: " + instanceID);
                System.out.println(instance);
            }
        }*/
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
        JSplitPane splitPane = new JSplitPane();
        frame.getContentPane().add(splitPane);

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

        browserTree.setPreferredSize(new Dimension(200, 600));
        browserPanel.add(browserTree, BorderLayout.CENTER);
        //frame.getContentPane().add(browserPanel, BorderLayout.WEST);
        splitPane.setLeftComponent(browserPanel);

        JPanel editorPanel = new JPanel();
        //frame.getContentPane().add(editorPanel, BorderLayout.CENTER);
        splitPane.setRightComponent(editorPanel);
        editorPanel.setLayout(new BorderLayout());

        frame.setVisible(true);
        //frame.setResizable(false);

        EditorElement roomElement = buildMenuForTemplate(editorPanel, templates.get("room"));
        editorPanel.add(roomElement);
        roomElement.setData(data.get("room").get("wilsons_corner_store"));

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private EditorElement buildMenuForTemplate(JPanel containingPanel, Template template) {
        /*JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder(template.name()));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        containingPanel.add(mainPanel, BorderLayout.CENTER);
        for (TemplateParameter parameter : template.parameters()) {
            mainPanel.add(ParameterFactory.create(parameter, templates, data));
        }*/
        return new ParameterFieldObject(template.name(), template, templates, data);
        //containingPanel.add(objectPanel);
    }

}