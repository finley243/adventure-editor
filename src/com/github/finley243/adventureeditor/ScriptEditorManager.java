package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataScript;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.ScriptEditorFrame;

import javax.swing.*;
import java.util.*;

public class ScriptEditorManager implements DataSaveTarget {

    private static final Template SCRIPT_TEMPLATE = new Template("script", "Script", false, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("name", TemplateParameter.ParameterDataType.STRING, "Name", null, false, false, null, null, new ArrayList<>(), false, null, 0, 0, 1, 1, null));
        add(new TemplateParameter("script", TemplateParameter.ParameterDataType.SCRIPT, "Script", null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null);

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
        EditorFrame editorFrame = new EditorFrame(main, scriptEditorFrame, SCRIPT_TEMPLATE, null, this, true);
        editorFrame.setResizable(true);
        childFrameHandler.add(null, editorFrame);
    }

    public void editScript(String phraseKey) {
        boolean isAlreadyOpen = childFrameHandler.requestFocusIfOpen(phraseKey);
        if (!isAlreadyOpen) {
            Data initialData = generateDataForScript(phraseKey);
            EditorFrame editorFrame = new EditorFrame(main, scriptEditorFrame, SCRIPT_TEMPLATE, initialData, this, true);
            editorFrame.setResizable(true);
            childFrameHandler.add(phraseKey, editorFrame);
        }
    }

    /*public void duplicateScript(String phraseKey) {
        String newKey = generateDuplicatePhraseKey(phraseKey);
        String phrase = phrases.get(phraseKey);
        phrases.put(newKey, phrase);
        onUpdatePhrases();
    }*/

    public void deleteScript(String scriptName) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(scriptEditorFrame, "Are you sure you want to delete " + scriptName + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            scripts.remove(scriptName);
            onUpdateScripts();
        }
    }

    @Override
    public void saveObjectData(Data data, Data initialData) {
        if (initialData != null) {
            String initialName = ((DataString) ((DataObject) initialData).getValue().get("name")).getValue();
            scripts.remove(initialName);
        }
        String scriptName = ((DataString) ((DataObject) data).getValue().get("name")).getValue();
        String scriptBody = ((DataScript) ((DataObject) data).getValue().get("script")).getValue();
        scripts.put(scriptName, scriptBody);
        onUpdateScripts();
        scriptEditorFrame.selectScript(scriptName);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        childFrameHandler.removeChildFrame(frame);
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        String scriptName = ((DataString) ((DataObject) currentData).getValue().get("name")).getValue();
        if (scriptName.trim().isEmpty()) {
            return new ErrorData(true, "Script name cannot be empty.");
        }
        /*String scriptBody = ((DataString) ((DataObject) currentData).getValue().get("script")).getValue();
        if (scriptBody.trim().isEmpty()) {
            return new ErrorData(true, "Script body cannot be empty.");
        }*/
        String initialKey = initialData == null ? null : ((DataString) ((DataObject) initialData).getValue().get("name")).getValue();
        if (scripts.containsKey(scriptName) && !Objects.equals(scriptName, initialKey)) {
            return new ErrorData(true, "A phrase with the key " + scriptName + " already exists.");
        }
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
