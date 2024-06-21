package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class EditorFrame extends JFrame {

    private final Main main;
    private final EditorElement editorElement;
    private final Template template;
    private final Data initialData;
    private final DataSaveTarget saveTarget;
    private final JButton saveButton;

    public EditorFrame(Main main, Template template, Data objectData, DataSaveTarget saveTarget) {
        super(template.name());
        if (saveTarget == null) {
            throw new IllegalArgumentException("Save target cannot be null");
        }
        this.main = main;
        this.template = template;
        this.initialData = objectData;
        this.saveTarget = saveTarget;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.editorElement = new ParameterFieldObject(this, false, template.name(), template, main, saveTarget instanceof BrowserFrame, true);
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

        EditorFrame thisFrame = this;
        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestClose(false, false, true);
            }
        };
        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestClose(true, false, false);
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("saveEditor", saveAction);
        actionMap.put("closeEditor", closeAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveEditor");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeEditor");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public boolean requestClose(boolean canCancel, boolean forceClose, boolean forceSave) {
        boolean editorElementClosed = editorElement.requestClose(false, forceClose, forceSave);
        if (!editorElementClosed) {
            return false;
        }
        if (forceClose || !hasUnsavedChanges()) {
            if (forceSave) {
                if (isDataValidOrShowDialog()) {
                    saveTarget.saveObjectData(editorElement.getData(), initialData);
                }
            }
            saveTarget.onEditorFrameClose(this);
            this.dispose();
            return true;
        }
        if (forceSave) {
            if (isDataValidOrShowDialog()) {
                saveTarget.saveObjectData(editorElement.getData(), initialData);
                saveTarget.onEditorFrameClose(this);
                this.dispose();
                return true;
            }
            return false;
        }
        String[] confirmOptions = canCancel ? new String[] {"Yes", "No", "Cancel"} : new String[] {"Yes", "No"};
        int confirmResult = JOptionPane.showOptionDialog(this, "Would you like to save changes?", "Save Confirmation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            // Save
            if (isDataValidOrShowDialog()) {
                saveTarget.saveObjectData(editorElement.getData(), initialData);
                saveTarget.onEditorFrameClose(this);
                this.dispose();
                return true;
            }
        } else if (confirmResult == 1) {
            // Don't save
            saveTarget.onEditorFrameClose(this);
            this.dispose();
            return true;
        }
        // Cancel (do nothing)
        return false;
    }

    public Template getTemplate() {
        return template;
    }

    public String getObjectID() {
        if (initialData == null) {
            return null;
        }
        return ((DataObject) initialData).getID();
    }

    private JPanel getButtonPanel(boolean isNewInstance) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        saveButton.addActionListener(e -> {
            requestClose(false, false, true);
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            requestClose(false, true, false);
        });
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
            requestClose(true, false, false);
        } else {
            super.processWindowEvent(e);
        }
    }

    // Returns true if data is valid, shows error dialog and returns false if not
    private boolean isDataValidOrShowDialog() {
        DataSaveTarget.ErrorData errorData = saveTarget.isDataValidOrShowDialog(editorElement.getData(), initialData);
        if (!errorData.hasError()) {
            return true;
        }
        JOptionPane.showMessageDialog(this, errorData.message(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean hasUnsavedChanges() {
        if (initialData == null) {
            return true;
        }
        Data currentData = editorElement.getData();
        return !initialData.equals(currentData);
    }

}
