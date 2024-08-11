package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataTreeBranch;
import com.github.finley243.adventureeditor.template.Group;
import com.github.finley243.adventureeditor.template.TabGroup;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.EditorGroup;
import com.github.finley243.adventureeditor.ui.EditorTabGroup;
import com.github.finley243.adventureeditor.ui.OptionalBorderedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParameterFieldObject extends ParameterField {

    private final Map<String, ParameterField> editorElements;
    private final Template template;

    public ParameterFieldObject(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, Template template, Main main, boolean isTopLevelEditor, boolean isSeparateWindow) {
        super(editorFrame, optional, name, parentField);
        this.template = template;
        JPanel objectPanel = new JPanel();
        /*if (!isSeparateWindow) {
            objectPanel.setBorder(BorderFactory.createTitledBorder(name));
        }*/
        getInnerPanel().setLayout(new BorderLayout());
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
            tabGroupConstraints.weightx = 1;
            tabGroupConstraints.weighty = 1;
            tabGroupConstraints.fill = GridBagConstraints.BOTH;
            tabGroupConstraints.anchor = GridBagConstraints.NORTHWEST;
            //tabGroupConstraints.insets = new Insets(2, 2, 2, 2);
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
                groupConstraints.weightx = 1;
                groupConstraints.weighty = 1;
                groupConstraints.fill = GridBagConstraints.BOTH;
                groupConstraints.anchor = GridBagConstraints.NORTHWEST;
                //groupConstraints.insets = new Insets(2, 2, 2, 2);
                objectPanel.add(editorGroup, groupConstraints);
            }
        }
        for (TemplateParameter parameter : template.parameters()) {
            if (isTopLevelEditor || !parameter.topLevelOnly()) {
                ParameterField parameterElement = ParameterFieldFactory.create(parameter, main, editorFrame, parentField);
                if (parameterElement == null) {
                    continue;
                }
                GridBagConstraints parameterConstraints = new GridBagConstraints();
                parameterConstraints.gridx = parameter.x();
                parameterConstraints.gridy = parameter.y();
                parameterConstraints.gridwidth = parameter.width();
                parameterConstraints.gridheight = parameter.height();
                parameterConstraints.weightx = 1;
                parameterConstraints.weighty = 1;
                parameterConstraints.fill = GridBagConstraints.BOTH;
                parameterConstraints.anchor = GridBagConstraints.NORTHWEST;
                //parameterConstraints.insets = new Insets(2, 2, 2, 2);
                editorElements.put(parameter.id(), parameterElement);
                /*if (parameterElement instanceof ParameterFieldObject) {
                    parameterElement.setBorder(BorderFactory.createDashedBorder(Color.RED));
                    parameterElement.getInnerPanel().setBorder(BorderFactory.createDashedBorder(Color.BLUE));
                }*/
                if (parameter.group() != null) {
                    EditorGroup group = groups.get(parameter.group());
                    if (group == null) {
                        throw new IllegalArgumentException("Group " + parameter.group() + " not found in template " + template.id());
                    }
                    //parameterConstraints.anchor = GridBagConstraints.NORTHWEST;
                    //parameterConstraints.fill = GridBagConstraints.HORIZONTAL;
                    group.add(parameterElement, parameterConstraints);
                } else {
                    objectPanel.add(parameterElement, parameterConstraints);
                }
            }
        }
        //getInnerPanel().add(objectPanel);
        if (optional) {
            getInnerPanel().add(new OptionalBorderedPanel(name, objectPanel, getOptionalCheckbox()), BorderLayout.CENTER);
        } else {
            setBorder(BorderFactory.createEmptyBorder());
            getInnerPanel().add(objectPanel, BorderLayout.CENTER);
        }
        if (optional) {
            setEnabledState(false);
        }
    }

    @Override
    public boolean requestClose(boolean forceClose, boolean forceSave) {
        for (ParameterField parameterField : editorElements.values()) {
            boolean didClose = parameterField.requestClose(forceClose, forceSave);
            if (!didClose) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setEnabledState(boolean enabled) {
        for (ParameterField element : editorElements.values()) {
            element.setEnabledFromParent(enabled);
        }
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        Map<String, Data> objectParameters = new HashMap<>();
        for (Map.Entry<String, ParameterField> entry : editorElements.entrySet()) {
            Data parameterData = entry.getValue().getData();
            objectParameters.put(entry.getKey(), parameterData);
        }
        for (TemplateParameter parameter : template.parameters()) {
            if (parameter.dataType() == TemplateParameter.ParameterDataType.TREE_BRANCH) {
                objectParameters.put(parameter.id(), new DataTreeBranch(new ArrayList<>()));
            } else if (!objectParameters.containsKey(parameter.id()) && parameter.defaultValue() != null) {
                objectParameters.put(parameter.id(), parameter.defaultValue().createCopy());
            }
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
            for (TemplateParameter parameter : template.parameters()) {
                if (!dataObject.getValue().containsKey(parameter.id())) {
                    editorElements.get(parameter.id()).setData(parameter.defaultValue());
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
