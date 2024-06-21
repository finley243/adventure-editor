package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.data.DataObject;
import com.github.finley243.adventureeditor.data.DataString;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import com.github.finley243.adventureeditor.ui.DataSaveTarget;
import com.github.finley243.adventureeditor.ui.EditorFrame;
import com.github.finley243.adventureeditor.ui.PhraseEditorFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PhraseEditorManager implements DataSaveTarget {

    private static final Template PHRASE_TEMPLATE = new Template("phrase", "Phrase", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("key", TemplateParameter.ParameterDataType.STRING, "Key", null, false, false, null, null, new ArrayList<>(), false, null, 0, 0, 1, 1, null));
        add(new TemplateParameter("text", TemplateParameter.ParameterDataType.STRING, "Phrase", null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null);

    private final Main main;
    private final Map<String, String> phrases;
    private final Map<String, EditorFrame> activeEditorFrames;
    private final List<EditorFrame> activeEditorFramesUnsaved;

    private PhraseEditorFrame phraseEditorFrame;

    public PhraseEditorManager(Main main) {
        this.main = main;
        this.phrases = new HashMap<>();
        this.activeEditorFrames = new HashMap<>();
        this.activeEditorFramesUnsaved = new ArrayList<>();
    }

    public Map<String, String> getPhrases() {
        return phrases;
    }

    public void openPhraseEditor() {
        if (!main.getProjectManager().isProjectLoaded()) {
            return;
        }
        if (phraseEditorFrame != null) {
            phraseEditorFrame.toFront();
            phraseEditorFrame.requestFocus();
        } else {
            phraseEditorFrame = new PhraseEditorFrame(main);
        }
    }

    public void onClosePhraseEditor() {
        phraseEditorFrame = null;
        for (EditorFrame editorFrame : activeEditorFrames.values()) {
            // TODO - Replace with a requestClose function on editor frames, which opens a save confirmation if necessary
            editorFrame.dispose();
        }
        for (EditorFrame editorFrame : activeEditorFramesUnsaved) {
            // TODO - Replace with a requestClose function on editor frames, which opens a save confirmation if necessary
            editorFrame.dispose();
        }
    }

    public void newPhrase() {
        EditorFrame editorFrame = new EditorFrame(main, PHRASE_TEMPLATE, null, this);
        activeEditorFramesUnsaved.add(editorFrame);
    }

    public void editPhrase(String phraseKey) {
        if (activeEditorFrames.containsKey(phraseKey)) {
            EditorFrame editorFrame = activeEditorFrames.get(phraseKey);
            editorFrame.toFront();
            editorFrame.requestFocus();
        } else {
            Data initialData = generateDataForPhrase(phraseKey);
            EditorFrame editorFrame = new EditorFrame(main, PHRASE_TEMPLATE, initialData, this);
            activeEditorFrames.put(phraseKey, editorFrame);
        }
    }

    public void duplicatePhrase(String phraseKey) {
        String newKey = generateDuplicatePhraseKey(phraseKey);
        String phrase = phrases.get(phraseKey);
        phrases.put(newKey, phrase);
        onUpdatePhrases();
    }

    public void deletePhrase(String phraseKey) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(phraseEditorFrame, "Are you sure you want to delete " + phraseKey + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            phrases.remove(phraseKey);
            onUpdatePhrases();
        }
    }

    @Override
    public void saveObjectData(Data data, Data initialData) {
        if (initialData != null) {
            String initialKey = ((DataString) ((DataObject) initialData).getValue().get("key")).getValue();
            phrases.remove(initialKey);
        }
        String newKey = ((DataString) ((DataObject) data).getValue().get("key")).getValue();
        String newPhrase = ((DataString) ((DataObject) data).getValue().get("text")).getValue();
        phrases.put(newKey, newPhrase);
        onUpdatePhrases();
        phraseEditorFrame.selectPhrase(newKey);
    }

    @Override
    public void onEditorFrameClose(EditorFrame frame) {
        if (activeEditorFramesUnsaved.contains(frame)) {
            activeEditorFramesUnsaved.remove(frame);
        } else {
            Iterator<Map.Entry<String, EditorFrame>> iterator = activeEditorFrames.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, EditorFrame> entry = iterator.next();
                if (entry.getValue().equals(frame)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    @Override
    public boolean isDataValidOrShowDialog(Component parentComponent, Data currentData, Data initialData) {
        String newKey = ((DataString) ((DataObject) currentData).getValue().get("key")).getValue();
        if (newKey.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Key cannot be empty.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String newPhrase = ((DataString) ((DataObject) currentData).getValue().get("text")).getValue();
        if (newPhrase.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Phrase cannot be empty.", "Invalid Phrase", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String initialKey = initialData == null ? null : ((DataString) ((DataObject) initialData).getValue().get("key")).getValue();
        if (phrases.containsKey(newKey) && !Objects.equals(newKey, initialKey)) {
            JOptionPane.showMessageDialog(parentComponent, "A phrase with the key " + newKey + " already exists.", "Duplicate Key", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private Data generateDataForPhrase(String phraseKey) {
        Map<String, Data> dataMap = new HashMap<>();
        dataMap.put("key", new DataString(phraseKey));
        dataMap.put("text", new DataString(phrases.get(phraseKey)));
        return new DataObject(PHRASE_TEMPLATE, dataMap);
    }

    private void onUpdatePhrases() {
        if (phraseEditorFrame != null) {
            phraseEditorFrame.reloadPhrases();
        }
    }

    private String generateDuplicatePhraseKey(String phraseKey) {
        Set<String> existingIDs = main.getPhraseEditorManager().getPhrases().keySet();
        String baseCopyID = phraseKey + "_COPY_";
        int i = 1;
        while (existingIDs.contains(baseCopyID + i)) {
            i += 1;
        }
        return baseCopyID + i;
    }

}
