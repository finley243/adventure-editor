package com.github.finley243.adventureeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ScriptEditorManager {

    private Map<String, String> scripts;
    private Map<String, String> lastSavedScripts;

    public ScriptEditorManager() {
        this.scripts = null;
    }

    public void loadScripts(Map<String, String> scripts) {
        this.scripts = new HashMap<>(scripts);
        setSavedChanges();
    }

    public void setScript(String name, String body) {
        scripts.put(name, body);
    }

    public String getScript(String name) {
        return scripts.get(name);
    }

    public Map<String, String> getScripts() {
        if (scripts == null) return Map.of();
        return new HashMap<>(scripts);
    }

    public void unloadScripts() {
        scripts = new HashMap<>();
        setSavedChanges();
    }

    public void removeScript(String name) {
        scripts.remove(name);
    }

    public Set<String> getScriptIDs() {
        return scripts.keySet();
    }

    public boolean hasScriptWithName(String name) {
        return scripts.containsKey(name);
    }

    public boolean hasUnsavedChanges() {
        return !Objects.equals(scripts, lastSavedScripts);
    }

    public void setSavedChanges() {
        this.lastSavedScripts = scripts == null ? null : new HashMap<>(scripts);
    }

}
