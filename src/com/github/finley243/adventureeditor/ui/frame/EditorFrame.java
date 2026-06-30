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
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.function.Function;

public class EditorFrame extends ThemedDialog {

    private final ParameterField parameterField;
    private final JLabel errorLabel;
    private final Template template;
    private final Data initialData;
    private final Consumer<Data> onSave;
    private final Function<Data, ErrorData> onValidate;
    private final Consumer<EditorFrame> onClose;

    public EditorFrame(Window parentWindow, Template template, Data objectData, boolean isTopLevel, ParameterFactory parameterFactory, PresenterActions presenter, Consumer<Data> onSave, Function<Data, ErrorData> onValidate, Consumer<EditorFrame> onClose) {
        //super(template.name());
        super(parentWindow, template.name());
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
        this.errorLabel = new JLabel(" ");
        this.errorLabel.setForeground(Color.RED);
        this.errorLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        mainPanel.add(errorLabel, BorderLayout.NORTH);
        this.parameterField = new ParameterFieldObject(this, false, template.name(), null, template, isTopLevel, parameterFactory, presenter);
        if (objectData != null) {
            updateData(objectData);
        }
        JScrollPane scrollPane = new JScrollPane(parameterField);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //this.setResizable(false);

        Action closeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestClose();
            }
        };

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("closeEditor", closeAction);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeEditor");

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void updateData(Data data) {
        parameterField.setData(data);
    }

    public boolean requestClose() {
        boolean subElementsClosed = parameterField.requestClose();
        if (!subElementsClosed) {
            return false;
        }
        onSave.accept(parameterField.getData());
        onClose.accept(this);
        this.dispose();
        return true;
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

    public void onEditorElementUpdated() {
        Data currentData = parameterField.getData();
        onSave.accept(currentData);
        updateErrorLabel(currentData);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            requestClose();
        } else {
            super.processWindowEvent(e);
        }
    }

    private boolean hasUnsavedChanges() {
        if (initialData == null) {
            return true;
        }
        Data currentData = parameterField.getData();
        return !initialData.equals(currentData);
    }

    private void updateErrorLabel(Data currentData) {
        ErrorData errorData = onValidate.apply(currentData);
        errorLabel.setText(errorData.hasError() ? errorData.message() : " ");
    }

}
