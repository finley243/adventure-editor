package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Group;
import com.github.finley243.adventureeditor.template.TabGroup;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ParameterFieldObject extends EditorElement {

    private final Map<String, EditorElement> editorElements;
    private final Template template;

    public ParameterFieldObject(EditorFrame editorFrame, boolean optional, String name, Template template, Main main, boolean isTopLevelEditor, boolean isSeparateWindow) {
        super(editorFrame, optional, name);
        this.template = template;
        JPanel objectPanel = new JPanel();
        /*if (!isSeparateWindow) {
            objectPanel.setBorder(BorderFactory.createTitledBorder(name));
        }*/
        objectPanel.setLayout(new GridBagLayout());
        this.editorElements = new HashMap<>();
        Map<String, EditorTabGroup> tabGroups = new HashMap<>();
        for (TabGroup tabGroup : template.tabGroups()) {
            EditorTabGroup editorTabGroup = new EditorTabGroup(tabGroup.id(), tabGroup.name());
            tabGroups.put(tabGroup.id(), editorTabGroup);
            GridBagConstraints tabGroupConstraints = new GridBagConstraints();
            tabGroupConstraints.gridx = tabGroup.x();
            tabGroupConstraints.gridy = tabGroup.y();
            tabGroupConstraints.gridwidth = tabGroup.width();
            tabGroupConstraints.gridheight = tabGroup.height();
            objectPanel.add(editorTabGroup, tabGroupConstraints);
        }
        Map<String, EditorGroup> groups = new HashMap<>();
        for (Group group : template.groups()) {
            EditorGroup editorGroup = new EditorGroup(group.id(), group.name());
            groups.put(group.id(), editorGroup);
            if (group.tabGroup() != null) {
                EditorTabGroup tabGroup = tabGroups.get(group.tabGroup());
                if (tabGroup == null) {
                    throw new IllegalArgumentException("Tab group " + group.tabGroup() + " not found in template " + template.id());
                }
                tabGroup.addGroupTab(editorGroup);
            } else {
                GridBagConstraints groupConstraints = new GridBagConstraints();
                groupConstraints.gridx = group.x();
                groupConstraints.gridy = group.y();
                groupConstraints.gridwidth = group.width();
                groupConstraints.gridheight = group.height();
                objectPanel.add(editorGroup, groupConstraints);
            }
        }
        for (TemplateParameter parameter : template.parameters()) {
            if (isTopLevelEditor || !parameter.topLevelOnly()) {
                EditorElement parameterElement = ParameterFactory.create(parameter, main, editorFrame);
                GridBagConstraints parameterConstraints = new GridBagConstraints();
                parameterConstraints.gridx = parameter.x();
                parameterConstraints.gridy = parameter.y();
                parameterConstraints.gridwidth = parameter.width();
                parameterConstraints.gridheight = parameter.height();
                editorElements.put(parameter.id(), parameterElement);
                if (parameter.group() != null) {
                    EditorGroup group = groups.get(parameter.group());
                    if (group == null) {
                        throw new IllegalArgumentException("Group " + parameter.group() + " not found in template " + template.id());
                    }
                    group.add(parameterElement, parameterConstraints);
                } else {
                    objectPanel.add(parameterElement, parameterConstraints);
                }
            }
        }
        //getInnerPanel().add(objectPanel);
        if (optional) {
            getInnerPanel().add(new OptionalBorderedPanel(name, objectPanel, getOptionalCheckbox()));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
            getInnerPanel().add(objectPanel);
        }
    }

    @Override
    public void setEnabledState(boolean enabled) {
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
