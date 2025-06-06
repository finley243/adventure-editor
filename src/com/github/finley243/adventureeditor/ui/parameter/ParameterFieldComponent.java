package com.github.finley243.adventureeditor.ui.parameter;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataComponent;
import com.github.finley243.adventureeditor.template.*;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFieldComponent extends ParameterField {

    private final Map<String, ParameterField> editorElements;
    private final JPanel objectPanel;
    private final JComboBox<ComponentOption> dropdownMenu;
    private final Map<String, ComponentOption> componentOptionMap;

    private String activeOption;

    public ParameterFieldComponent(EditorFrame editorFrame, boolean optional, String name, ParameterField parentField, List<ComponentOption> componentOptions, Main main) {
        super(editorFrame, optional, name, parentField);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.editorElements = new HashMap<>();
        this.objectPanel = new JPanel();
        this.componentOptionMap = new HashMap<>();
        objectPanel.setLayout(new CardLayout());
        objectPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        for (ComponentOption option : componentOptions) {
            ParameterField element = new ParameterFieldObject(editorFrame, false, option.name(), this, main.getTemplate(option.object()), main, false);
            objectPanel.add(element, option.id());
            editorElements.put(option.id(), element);
            componentOptionMap.put(option.id(), option);
        }
        ComponentOption[] optionsArray = componentOptions.toArray(new ComponentOption[0]);
        this.dropdownMenu = new JComboBox<>(optionsArray);
        dropdownMenu.setPreferredSize(new Dimension(150, 20));
        dropdownMenu.addActionListener(e -> {
            ComponentOption selectedOption = (ComponentOption) dropdownMenu.getSelectedItem();
            CardLayout cardLayout = (CardLayout) objectPanel.getLayout();
            cardLayout.show(objectPanel, selectedOption.id());
            activeOption = selectedOption.id();
            onFieldUpdated();
        });
        setActiveOption(componentOptions.getFirst().id());
        getInnerPanel().setLayout(new GridBagLayout());
        GridBagConstraints optionalConstraints = new GridBagConstraints();
        optionalConstraints.gridx = 0;
        optionalConstraints.gridy = 0;
        GridBagConstraints dropdownConstraints = new GridBagConstraints();
        dropdownConstraints.gridx = 0;
        dropdownConstraints.gridy = optional ? 1 : 0;
        dropdownConstraints.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = optional ? 2 : 1;
        panelConstraints.weightx = 1;
        panelConstraints.weighty = 1;
        panelConstraints.fill = GridBagConstraints.BOTH;
        if (optional) {
            getInnerPanel().add(getOptionalCheckbox(), optionalConstraints);
        }
        getInnerPanel().add(dropdownMenu, dropdownConstraints);
        getInnerPanel().add(objectPanel, panelConstraints);
        if (optional) {
            setEnabledState(false);
        }
    }

    public void setActiveOption(String option) {
        CardLayout cardLayout = (CardLayout) objectPanel.getLayout();
        cardLayout.show(objectPanel, option);
        dropdownMenu.setSelectedItem(componentOptionMap.get(option));
        activeOption = option;
    }

    @Override
    public void setEnabledState(boolean enabled) {
        for (ParameterField element : editorElements.values()) {
            element.setEnabledFromParent(enabled);
        }
        dropdownMenu.setEnabled(enabled);
    }

    @Override
    public Data getData() {
        if (!isOptionalEnabled()) {
            return null;
        }
        Data currentObjectData = editorElements.get(activeOption).getData();
        //String nameOverride = useComponentTypeName ? componentOptionMap.get(activeOption).name() : null;
        return new DataComponent(activeOption, currentObjectData, null);
    }

    @Override
    public void setData(Data data) {
        setOptionalEnabled(data != null);
        if (data instanceof DataComponent dataComponent) {
            setActiveOption(dataComponent.getType());
            editorElements.get(activeOption).setData(dataComponent.getObjectData());
        }
    }

}
