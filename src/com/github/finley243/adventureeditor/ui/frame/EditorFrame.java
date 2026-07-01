package com.github.finley243.adventureeditor.ui.frame;

import com.github.finley243.adventureeditor.PresenterActions;
import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.ui.EditorSession;
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

public class EditorFrame extends ThemedDialog implements EditorSession {

    private final ParameterField parameterField;
    private final JLabel errorLabel;
    private final Template template;
    private final Data initialData;
    private final Consumer<Data> onSave;
    private final Function<Data, ErrorData> onValidate;
    private final Consumer<EditorFrame> onClose;

    private String currentKey;
    private Data currentData;

    public EditorFrame(Window parentWindow, Template template, Data objectData, String initialKey, boolean isTopLevel, ParameterFactory parameterFactory, PresenterActions presenter, Consumer<Data> onInitialize, Consumer<Data> onSave, Function<Data, ErrorData> onValidate, Consumer<EditorFrame> onClose) {
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

        Data fullInitialData = parameterField.getData();
        this.currentKey = initialKey;
        this.currentData = fullInitialData;
        onInitialize.accept(fullInitialData);
        updateErrorLabel(fullInitialData);
    }

    @Override
    public String getCurrentKey() {
        return currentKey;
    }

    @Override
    public void setCurrentKey(String key) {
        this.currentKey = key;
    }

    @Override
    public Data getCurrentData() {
        return currentData;
    }

    @Override
    public void setCurrentData(Data data) {
        this.currentData = data;
    }

    public void refreshData(String newKey, Data newData) {
        this.currentKey = newKey;
        this.currentData = newData;
        parameterField.setData(newData);
    }

    public void updateData(Data data) {
        parameterField.setData(data);
    }

    public void requestClose() {
        parameterField.requestClose();
        onSave.accept(parameterField.getData());
        onClose.accept(this);
        this.dispose();
    }

    public void disposeWithoutSaving() {
        parameterField.requestClose();
        onClose.accept(this);
        this.dispose();
    }

    public Template getTemplate() {
        return template;
    }

    public void onEditorElementUpdated() {
        Data currentData = parameterField.getData();
        updateErrorLabel(currentData);
        onSave.accept(currentData);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            requestClose();
        } else {
            super.processWindowEvent(e);
        }
    }

    private void updateErrorLabel(Data currentData) {
        ErrorData errorData = onValidate.apply(currentData);
        errorLabel.setText(errorData.hasError() ? errorData.message() : " ");
    }

}
