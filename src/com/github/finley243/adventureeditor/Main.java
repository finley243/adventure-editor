package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateRegistry;
import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.frame.MainFrame;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;

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