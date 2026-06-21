package com.github.finley243.adventureeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PhraseEditorManager {

    private Map<String, String> phrases;
    private Map<String, String> lastSavedPhrases;

    public PhraseEditorManager() {
        this.phrases = null;
    }

    public String getPhrase(String key) {
        return phrases.get(key);
    }

    public Map<String, String> getPhrases() {
        if (phrases == null) return Map.of();
        return new HashMap<>(phrases);
    }

    public void loadPhrases(Map<String, String> phrases) {
        this.phrases = new HashMap<>(phrases);
        setSavedChanges();
    }

    public void unloadPhrases() {
        phrases = new HashMap<>();
        setSavedChanges();
    }

    public boolean hasPhraseWithKey(String key) {
        return phrases.containsKey(key);
    }

    public void setPhrase(String key, String text) {
        phrases.put(key, text);
    }

    public void removePhrase(String key) {
        phrases.remove(key);
    }

    public Set<String> getPhraseIDs() {
        return phrases.keySet();
    }

    public boolean hasUnsavedChanges() {
        return !Objects.equals(phrases, lastSavedPhrases);
    }

    public void setSavedChanges() {
        this.lastSavedPhrases = phrases == null ? null : new HashMap<>(phrases);
    }

}
