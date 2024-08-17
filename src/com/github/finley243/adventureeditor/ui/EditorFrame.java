package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.Main;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.parameter.ParameterField;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFieldObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditorFrame extends JDialog {

    private final Main main;
    private final String editorID;
    private final ParameterField parameterField;
    private final Template template;
    private final Data initialData;
    private final DataSaveTarget saveTarget;
    private final JButton saveButton;

    public EditorFrame(Main main, String editorID, Window parentWindow, Template template, Data objectData, DataSaveTarget saveTarget, boolean isTopLevel) {
        //super(template.name());
        super(parentWindow);
        //this.setAutoRequestFocus(false);
        this.setTitle(template.name());
        this.setModalityType(ModalityType.MODELESS);
        if (saveTarget == null) {
            throw new IllegalArgumentException("Save target cannot be null");
        }
        this.main = main;
        this.editorID = editorID;
        this.template = template;
        this.initialData = objectData;
        this.saveTarget = saveTarget;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.parameterField = new ParameterFieldObject(this, false, template.name(), null, template, main, isTopLevel, true);
        if (objectData != null) {
            parameterField.setData(objectData);
        }
        JScrollPane scrollPane = new JScrollPane(parameterField);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        boolean isNewInstance = objectData == null;
        this.saveButton = new JButton("OK");
        saveButton.setEnabled(isNewInstance);
        JPanel buttonPanel = getButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);

        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestClose(false, true);
            }
        };
        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestClose(true, false);
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

    public boolean requestClose(boolean forceClose, boolean forceSave) {
        if (forceSave) {
            boolean subElementsClosed = parameterField.requestClose(false, false);
            if (!subElementsClosed) {
                return false;
            }
            if (isDataValidOrShowDialog()) {
                saveTarget.saveObjectData(editorID, parameterField.getData(), initialData);
                saveTarget.onEditorFrameClose(this);
                this.dispose();
                return true;
            }
            return true;
        } else if (forceClose) {
            boolean subElementsClosed = parameterField.requestClose(true, false);
            if (!subElementsClosed) {
                return false;
            }
            saveTarget.onEditorFrameClose(this);
            this.dispose();
            return true;
        }
        boolean subElementsClosed = parameterField.requestClose(false, false);
        if (!subElementsClosed) {
            return false;
        }
        boolean hasUnsavedChanges = hasUnsavedChanges();
        if (!hasUnsavedChanges) {
            saveTarget.onEditorFrameClose(this);
            this.dispose();
            return true;
        }
        String[] confirmOptions = new String[] {"Yes", "No", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(this, "Would you like to save changes?", "Save Confirmation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            // Save
            boolean editorElementClosed = parameterField.requestClose(false, false);
            if (!editorElementClosed) {
                return false;
            }
            if (isDataValidOrShowDialog()) {
                saveTarget.saveObjectData(editorID, parameterField.getData(), initialData);
                saveTarget.onEditorFrameClose(this);
                this.dispose();
                return true;
            }
        } else if (confirmResult == 1) {
            // Don't save
            boolean editorElementClosed = parameterField.requestClose(true, false);
            if (!editorElementClosed) {
                return false;
            }
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

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        buttonPanel.add(Box.createHorizontalGlue());
        saveButton.addActionListener(e -> {
            requestClose(false, true);
        });
        saveButton.setPreferredSize(new Dimension(100, saveButton.getPreferredSize().height));
        buttonPanel.add(saveButton);
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
            requestClose(true, false);
        } else {
            super.processWindowEvent(e);
        }
    }

    // Returns true if data is valid, shows error dialog and returns false if not
    private boolean isDataValidOrShowDialog() {
        DataSaveTarget.ErrorData errorData = saveTarget.isDataValidOrShowDialog(parameterField.getData(), initialData);
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
        Data currentData = parameterField.getData();
        return !initialData.equals(currentData);
    }

}
