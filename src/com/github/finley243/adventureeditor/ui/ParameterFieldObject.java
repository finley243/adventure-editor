package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ParameterFieldObject extends EditorElement {

    private final Map<String, EditorElement> editorElements;
    private final Template template;
    private final JPanel objectPanel;

    public ParameterFieldObject(EditorFrame editorFrame, boolean optional, String name, Template template, Main main, boolean isTopLevelEditor, boolean isSeparateWindow) {
        super(editorFrame, optional, name);
        this.template = template;
        this.objectPanel = new JPanel();
        if (!isSeparateWindow) {
            objectPanel.setBorder(BorderFactory.createTitledBorder(name));
        }
        //objectPanel.setLayout(new BoxLayout(objectPanel, BoxLayout.Y_AXIS));
        //objectPanel.setLayout(new GridLayout(0, 1));
        objectPanel.setLayout(new GridBagLayout());
        this.editorElements = new HashMap<>();
        for (TemplateParameter parameter : template.parameters()) {
            if (isTopLevelEditor || !parameter.topLevelOnly()) {
                EditorElement parameterElement = ParameterFactory.create(parameter, main, editorFrame);
                GridBagConstraints parameterConstraints = new GridBagConstraints();
                parameterConstraints.gridx = parameter.x();
                parameterConstraints.gridy = parameter.y();
                parameterConstraints.gridwidth = parameter.width();
                parameterConstraints.gridheight = parameter.height();
                objectPanel.add(parameterElement, parameterConstraints);
                editorElements.put(parameter.id(), parameterElement);
            }
        }
        getInnerPanel().add(objectPanel);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        //objectPanel.setVisible(enabled);
        for (EditorElement element : editorElements.values()) {
            element.setEnabledFromParent(enabled);
        }

    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        Map<String, Data> objectParameters = new HashMap<>();
        for (Map.Entry<String, EditorElement> entry : editorElements.entrySet()) {
            Data parameterData = entry.getValue().getData();
            objectParameters.put(entry.getKey(), parameterData);
        }
        return new DataObject(template, objectParameters);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataObject dataObject) {
            for (Map.Entry<String, Data> entry : dataObject.getValue().entrySet()) {
                if (editorElements.containsKey(entry.getKey())) {
                    editorElements.get(entry.getKey()).setData(entry.getValue());
                }
            }
        }
    }

    @Override
    public String toString() {
        if (editorElements.containsKey("id")) {
            return editorElements.get("id").toString();
        }
        return template.name();
    }

}
