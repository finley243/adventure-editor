package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        /*try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
        int fontSize = 14;
        UIManager.getDefaults().keySet().forEach(key -> {
            Object value = UIManager.get(key);
            if (value instanceof Font font) {
                int fontStyle = font.getStyle();
                if (fontStyle == Font.BOLD) {
                    fontStyle = Font.PLAIN;
                } else if (fontStyle == Font.BOLD + Font.ITALIC) {
                    fontStyle = Font.ITALIC;
                }
                UIManager.put(key, new Font("Noto Sans", fontStyle, fontSize));
            }
        });
        UIManager.put("Label.font", new Font("Noto Sans", Font.BOLD, fontSize));

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        DataLoader dataLoader = new DataLoader();
        Map<String, Template> templateMap = dataLoader.loadTemplates();
        Map<String, List<String>> enumTypeMap = dataLoader.loadEnumTypes();
        TemplateRegistry templateRegistry = new TemplateRegistry(templateMap, enumTypeMap);
        PhraseEditorManager phraseEditorManager = new PhraseEditorManager();
        ScriptEditorManager scriptEditorManager = new ScriptEditorManager();
        ConfigMenuManager configMenuManager = new ConfigMenuManager(templateRegistry.getConfigTemplate());
        DataManager dataManager = new DataManager();
        ParameterFactory parameterFactory = new ParameterFactory(templateRegistry, dataManager);
        ProjectManager projectManager = new ProjectManager();
        MainFrame mainFrame = new MainFrame(parameterFactory, templateRegistry);
        mainFrame.setVisible(true);
        Presenter presenter = new Presenter(dataManager, projectManager, configMenuManager, phraseEditorManager, scriptEditorManager, templateRegistry, dataLoader, mainFrame);
        mainFrame.registerPresenter(presenter);
        presenter.start();
    }

}