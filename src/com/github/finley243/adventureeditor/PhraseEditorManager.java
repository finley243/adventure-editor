package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.PhraseEditorFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PhraseEditorManager {

    private final Main main;
    private final Map<String, String> phrases;

    private PhraseEditorFrame phraseEditorFrame;

    public PhraseEditorManager(Main main) {
        this.main = main;
        this.phrases = new HashMap<>();
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
    }

    public String newPhrase() {
        onUpdatePhrases();
        return null;
    }

    public void editPhrase(String key) {
        onUpdatePhrases();
    }

    public void duplicatePhrase(String key) {
        String newKey = generateDuplicatePhraseKey(key);
        String phrase = phrases.get(key);
        phrases.put(newKey, phrase);
        onUpdatePhrases();
    }

    public void deletePhrase(String key) {
        Object[] confirmOptions = {"Delete", "Cancel"};
        int confirmResult = JOptionPane.showOptionDialog(phraseEditorFrame, "Are you sure you want to delete " + key + "?", "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            phrases.remove(key);
            onUpdatePhrases();
        }
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
