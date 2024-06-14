package com.github.finley243.adventureeditor.data;

import java.util.Map;

public class DataObject extends Data {

    private final Map<String, Data> value;

    public DataObject(Map<String, Data> value) {
        this.value = value;
    }

    public Map<String, Data> getValue() {
        return value;
    }

    public String getID() {
        if (!value.containsKey("id") || !(value.get("id") instanceof DataString)) {
            return null;
        }
        return ((DataString) value.get("id")).getValue();
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
