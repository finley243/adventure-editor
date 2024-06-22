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
import java.util.*;

public class PhraseEditorManager implements DataSaveTarget {

    private static final Template PHRASE_TEMPLATE = new Template("phrase", "Phrase", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("key", TemplateParameter.ParameterDataType.STRING, "Key", null, false, false, null, null, new ArrayList<>(), false, null, 0, 0, 1, 1, null));
        add(new TemplateParameter("text", TemplateParameter.ParameterDataType.STRING_LONG, "Phrase", null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null);

    private final Main main;
    private final Map<String, String> phrases;
    private final ChildFrameHandler<String> childFrameHandler;

    private PhraseEditorFrame phraseEditorFrame;

    public PhraseEditorManager(Main main) {
        this.main = main;
        this.phrases = new HashMap<>();
        this.childFrameHandler = new ChildFrameHandler<>();
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

    public boolean onClosePhraseEditor() {
        boolean didCloseAll = childFrameHandler.closeAll();
        if (didCloseAll) {
            phraseEditorFrame = null;
            return true;
        }
        return false;
    }

    public void newPhrase() {
        EditorFrame editorFrame = new EditorFrame(main, PHRASE_TEMPLATE, null, this);
        childFrameHandler.add(null, editorFrame);
    }

    public void editPhrase(String phraseKey) {
        boolean isAlreadyOpen = childFrameHandler.requestFocusIfOpen(phraseKey);
        if (!isAlreadyOpen) {
            Data initialData = generateDataForPhrase(phraseKey);
            EditorFrame editorFrame = new EditorFrame(main, PHRASE_TEMPLATE, initialData, this);
            childFrameHandler.add(phraseKey, editorFrame);
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
        childFrameHandler.removeChildFrame(frame);
    }

    @Override
    public ErrorData isDataValidOrShowDialog(Data currentData, Data initialData) {
        String newKey = ((DataString) ((DataObject) currentData).getValue().get("key")).getValue();
        if (newKey.trim().isEmpty()) {
            return new ErrorData(true, "Key cannot be empty.");
        }
        String newPhrase = ((DataString) ((DataObject) currentData).getValue().get("text")).getValue();
        if (newPhrase.trim().isEmpty()) {
            return new ErrorData(true, "Phrase cannot be empty.");
        }
        String initialKey = initialData == null ? null : ((DataString) ((DataObject) initialData).getValue().get("key")).getValue();
        if (phrases.containsKey(newKey) && !Objects.equals(newKey, initialKey)) {
            return new ErrorData(true, "A phrase with the key " + newKey + " already exists.");
        }
        return new ErrorData(false, null);
    }

    public boolean hasChangesFrom(Map<String, String> lastSavedPhrases) {
        return !Objects.equals(phrases, lastSavedPhrases);
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
