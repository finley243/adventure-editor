package com.github.finley243.adventureeditor.data;

import com.github.finley243.adventureeditor.template.Template;

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
