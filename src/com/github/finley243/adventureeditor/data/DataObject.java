package com.github.finley243.adventureeditor.data;

import com.github.finley243.adventureeditor.template.Template;

import java.util.HashMap;
import java.util.Map;

public class DataObject extends Data {

    private final Template template;
    private final Map<String, Data> value;

    public DataObject(Template template, Map<String, Data> value) {
        this.template = template;
        this.value = value;
    }

    public Template getTemplate() {
        return template;
    }

    public Map<String, Data> getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        Map<String, Data> copyMap = new HashMap<>();
        for (Map.Entry<String, Data> entry : value.entrySet()) {
            copyMap.put(entry.getKey(), entry.getValue().createCopy());
        }
        return new DataObject(template, copyMap);
    }

    public void replaceID(String newID) {
        if (value.containsKey("id")) {
            value.put("id", new DataString(newID));
        }
    }

    public String getID() {
        if (!value.containsKey("id") || !(value.get("id") instanceof DataString)) {
            return null;
        }
        return ((DataString) value.get("id")).getValue();
    }

    @Override
    public String toString() {
        Data primaryParameterData = value.get(template.primaryParameter());
        if (primaryParameterData != null) {
            return primaryParameterData.toString();
        }
        String id = getID();
        if (id != null) {
            return id;
        }
        return template.id();
    }

}
