package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class EditorFrame extends JFrame {

    private final Main main;
    private final EditorElement editorElement;
    private final Data initialData;
    private final DataSaveTarget saveTarget;
    private final JButton saveButton;

    public EditorFrame(Main main, Template template, Data objectData, DataSaveTarget saveTarget) {
        super(template.name());
        this.main = main;
        this.initialData = objectData;
        this.saveTarget = saveTarget;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.editorElement = new ParameterFieldObject(this, false, template.name(), template, main, saveTarget == null, true);
        if (objectData != null) {
            editorElement.setData(objectData);
        }
        JScrollPane scrollPane = new JScrollPane(editorElement);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        boolean isNewInstance = objectData == null;
        this.saveButton = new JButton("Save");
        saveButton.setEnabled(isNewInstance);
        JPanel buttonPanel = getButtonPanel(isNewInstance);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private boolean isTopLevel() {
        return saveTarget == null;
    }

    private JPanel getButtonPanel(boolean isNewInstance) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        saveButton.addActionListener(e -> {
            if (isTopLevel()) {
                if (isDataValidOrShowDialog()) {
                    main.saveData(editorElement.getData(), initialData);
                    this.dispose();
                }
            } else {
                if (isDataValidOrShowDialog()) {
                    saveTarget.saveObjectData(editorElement.getData(), initialData);
                    this.dispose();
                }
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    // TODO - Replace calls using PropertyChanceListener with calls that are only made when values are changed
    public void onEditorElementUpdated() {
        if (saveButton != null) {
            saveButton.setEnabled(hasUnsavedChanges());
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (!hasUnsavedChanges()) {
                super.processWindowEvent(e);
                return;
            }
            String[] confirmOptions = {"Yes", "No", "Cancel"};
            int confirmResult = JOptionPane.showOptionDialog(this, "Would you like to save changes?", "Save Confirmation",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[2]);
            if (confirmResult == 0) {
                // Save
                if (isDataValidOrShowDialog()) {
                    main.saveData(editorElement.getData(), initialData);
                    super.processWindowEvent(e);
                }
            } else if (confirmResult == 1) {
                // Don't save
                super.processWindowEvent(e);
            }
            // Cancel (do nothing)
        } else {
            super.processWindowEvent(e);
        }
    }

    // Returns true if data is valid, shows error dialog and returns false if not
    private boolean isDataValidOrShowDialog() {
        boolean isNewInstance = initialData == null;
        Data currentData = editorElement.getData();
        if (isTopLevel()) {
            String categoryID = ((DataObject) currentData).getTemplate().id();
            String currentID = ((DataObject) currentData).getID();
            if (currentID == null || currentID.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (isNewInstance) {
                if (main.getIDsForCategory(categoryID).contains(currentID)) {
                    JOptionPane.showMessageDialog(this, "An object with ID \"" + currentID + "\" already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                String initialID = ((DataObject) initialData).getID();
                if (!initialID.equals(currentID) && main.getIDsForCategory(categoryID).contains(currentID)) {
                    JOptionPane.showMessageDialog(this, "An object with ID \"" + currentID + "\" already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        // TODO - Add checks for required parameters
        return true;
    }

    private boolean hasUnsavedChanges() {
        if (initialData == null) {
            return true;
        }
        Data currentData = editorElement.getData();
        return !initialData.equals(currentData);
    }

}