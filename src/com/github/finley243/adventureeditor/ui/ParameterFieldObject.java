package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ParameterFieldObject extends EditorElement {

    private final Map<String, EditorElement> editorElements;
    private final Template template;

    public ParameterFieldObject(String name, Template template, Main main, boolean isTopLevelEditor) {
        this.template = template;
        JPanel innerPanel = new JPanel();
        innerPanel.setBorder(BorderFactory.createTitledBorder(name));
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        this.editorElements = new HashMap<>();
        for (TemplateParameter parameter : template.parameters()) {
            if (isTopLevelEditor || !parameter.topLevelOnly()) {
                EditorElement parameterElement = ParameterFactory.create(parameter, main);
                innerPanel.add(parameterElement);
                editorElements.put(parameter.id(), parameterElement);
            }
        }
        add(innerPanel);
    }

    @Override
    public String toString() {
        if (editorElements.containsKey("id")) {
            return editorElements.get("id").toString();
        }
        return template.name();
    }

    @Override
    public Data getData() {
        Map<String, Data> objectParameters = new HashMap<>();
        for (Map.Entry<String, EditorElement> entry : editorElements.entrySet()) {
            objectParameters.put(entry.getKey(), entry.getValue().getData());
        }
        return new DataObject(template, objectParameters);
    }

    @Override
    public void setData(Data data) {
        if (data instanceof DataObject dataObject) {
            for (Map.Entry<String, Data> entry : dataObject.getValue().entrySet()) {
                if (editorElements.containsKey(entry.getKey())) {
                    editorElements.get(entry.getKey()).setData(entry.getValue());
                }
            }
        }
    }

}
