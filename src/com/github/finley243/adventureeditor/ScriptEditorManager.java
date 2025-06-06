package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.frame.EditorFrame;
import com.github.finley243.adventureeditor.ui.frame.ScriptEditorFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ScriptEditorManager implements DataSaveTarget {

    private static final Template SCRIPT_TEMPLATE = new Template("script", "Script", false, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("script", TemplateParameter.ParameterDataType.SCRIPT, null, null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null, null);

    private final Main main;
    private final Map<String, String> scripts;
    private final ChildFrameHandler<String> childFrameHandler;

    private ScriptEditorFrame scriptEditorFrame;

    public ScriptEditorManager(Main main) {
        this.main = main;
        this.scripts = new HashMap<>();
        this.childFrameHandler = new ChildFrameHandler<>();
    }

    public Map<String, String> getScripts() {
        return scripts;
    }

    public void openScriptEditor() {
        if (!main.getProjectManager().isProjectLoaded()) {
            return;
        }
        if (scriptEditorFrame != null) {
            scriptEditorFrame.toFront();
            scriptEditorFrame.requestFocus();
        } else {
            scriptEditorFrame = new ScriptEditorFrame(main);
        }
    }

    public boolean onCloseScriptEditor() {
        boolean didCloseAll = childFrameHandler.closeAll();
        if (didCloseAll) {
            scriptEditorFrame = null;
            return true;
        }
        return false;
    }

    public void newScript() {
        String scriptName = JOptionPane.showInputDialog(scriptEditorFrame, "Enter a name for the new script:");
        if (scriptName == null) {
            return;
        } else if (scriptName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(scriptEditorFrame, "Script name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (scripts.containsKey(scriptName)) {
            JOptionPane.showMessageDialog(scriptEditorFrame, "A script with the name " + scriptName + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EditorFrame editorFrame = new EditorFrame(main, scriptName, scriptEditorFrame, SCRIPT_TEMPLATE, null, this, true);
        editorFrame.setResizable(true);
        editorFrame.setSize(new Dimension(800, 800));
        editorFrame.setLocationRelativeTo(null);
        childFrameHandler.add(null, editorFrame);
    }

    public void editScript(String scriptName) {
        boolean isAlreadyOpen = childFrameHandler.requestFocusIfOpen(scriptName);
        if (!isAlreadyOpen) {
            Data initialData = generateDataForScript(scriptName);
            EditorFrame editorFrame = new EditorFrame(main, scriptName, scriptEditorFrame, SCRIPT_TEMPLATE, initialData, this, true);
            editorFrame.setResizable(true);
            editorFrame.setSize(new Dimension(800, 800));
            editorFrame.setLocationRelativeTo(null);
            childFrameHandler.add(scriptName, editorFrame);
        }
    }

    public void deleteScript(String scriptName) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(scriptEditorFrame, "Are you sure you want to delete " + scriptName + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            scripts.remove(scriptName);
            onUpdateScripts();
        }
    }

    @Override
    public void saveObjectData(String editorID, Data data, Data initialData) {
        String scriptBody = ((DataScript) ((DataObject) data).getValue().get("script")).getValue();
        scripts.put(editorID, scriptBody);
        onUpdateScripts();
        scriptEditorFrame.selectScript(editorID);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        childFrameHandler.removeChildFrame(frame);
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        return new ErrorData(false, null);
    }

    public boolean hasChangesFrom(Map<String, String> lastSavedScripts) {
        return !Objects.equals(scripts, lastSavedScripts);
    }

    private Data generateDataForScript(String phraseKey) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put("name", new DataString(phraseKey));
        dataMap.put("script", new DataScript(scripts.get(phraseKey)));
        return new DataObject(SCRIPT_TEMPLATE, dataMap);
    }

    private void onUpdateScripts() {
        if (scriptEditorFrame != null) {
            scriptEditorFrame.reloadScripts();
        }
    }

    /*private String generateDuplicateScriptKey(String phraseKey) {
        Set<String> existingIDs = main.getPhraseEditorManager().getPhrases().keySet();
        String baseCopyID = phraseKey + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }*/

}
