package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.PresenterActions;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.ErrorData;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFactory;
import com.github.finley243.adventureeditor.ui.parameter.ParameterField;
import com.github.finley243.adventureeditor.ui.parameter.ParameterFieldObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.function.Function;

public class EditorFrame extends JDialog {

    private final ParameterField parameterField;
    private final Template template;
    private final Data initialData;
    private final JButton saveButton;
    private final Consumer<Data> onSave;
    private final Function<Data, ErrorData> onValidate;
    private final Consumer<EditorFrame> onClose;

    public EditorFrame(Window parentWindow, Template template, Data objectData, boolean isTopLevel, ParameterFactory parameterFactory, PresenterActions presenter, Consumer<Data> onSave, Function<Data, ErrorData> onValidate, Consumer<EditorFrame> onClose) {
        //super(template.name());
        super(parentWindow);
        this.onSave = onSave;
        this.onValidate = onValidate;
        this.onClose = onClose;
        //this.setAutoRequestFocus(false);
        this.setTitle(template.name());
        this.setModalityType(ModalityType.MODELESS);
        this.template = template;
        this.initialData = objectData;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.parameterField = new ParameterFieldObject(this, false, template.name(), null, template, isTopLevel, parameterFactory, presenter);
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
        //this.setResizable(false);

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
                onSave.accept(parameterField.getData());
                onClose.accept(this);
                this.dispose();
                return true;
            }
            return false;
        } else if (forceClose) {
            boolean subElementsClosed = parameterField.requestClose(true, false);
            if (!subElementsClosed) {
                return false;
            }
            onClose.accept(this);
            this.dispose();
            return true;
        }
        boolean subElementsClosed = parameterField.requestClose(false, false);
        if (!subElementsClosed) {
            return false;
        }
        boolean hasUnsavedChanges = hasUnsavedChanges();
        if (!hasUnsavedChanges) {
            onClose.accept(this);
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
                onSave.accept(parameterField.getData());
                onClose.accept(this);
                this.dispose();
                return true;
            }
        } else if (confirmResult == 1) {
            // Don't save
            boolean editorElementClosed = parameterField.requestClose(true, false);
            if (!editorElementClosed) {
                return false;
            }
            onClose.accept(this);
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
        ErrorData errorData = onValidate.apply(parameterField.getData());
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
