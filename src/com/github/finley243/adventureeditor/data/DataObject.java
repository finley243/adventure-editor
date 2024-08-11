package com.github.finley243.adventureeditor.data;

import com.github.finley243.adventureeditor.template.Template;

import java.util.*;

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
            copyMap.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().createCopy());
        }
        return new DataObject(template, copyMap);
    }

    public void replaceID(String newID) {
        if (value.containsKey("id")) {
            value.put("id", new DataString(newID));
        }
    }

    public void replaceValue(String key, Data newValue) {
        value.put(key, newValue);
    }

    public String getID() {
        if (!value.containsKey("id") || !(value.get("id") instanceof DataString)) {
            return null;
        }
        return ((DataString) value.get("id")).getValue();
    }

    /*@Override
    public String toString() {
        Data primaryParameterData = value.get(template.nameFormat());
        if (primaryParameterData != null) {
            return primaryParameterData.toString();
        }
        String id = getID();
        if (id != null) {
            return id;
        }
        return template.id();
    }*/

    @Override
    public String toString() {
        String nameString = template.nameFormat() == null ? template.name() : template.nameFormat();
        List<String> parameterKeys = new ArrayList<>(value.keySet());
        parameterKeys.sort(Comparator.comparingInt(String::length));
        for (String parameterKey : parameterKeys) {
            Data parameterValue = value.get(parameterKey);
            String parameterString = parameterValue == null ? "null" : parameterValue.toString();
            nameString = nameString.replace("$" + parameterKey, parameterString);
        }
        return nameString;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataObject dataObject)) {
            return false;
        }
        Set<String> combinedKeys = new HashSet<>(value.keySet());
        combinedKeys.addAll(dataObject.value.keySet());
        for (String parameterKey : combinedKeys) {
            if (!value.containsKey(parameterKey) && dataObject.value.get(parameterKey) != null) {
                //System.out.println("Non-matching object parameter data (parameter only present in original data): " + parameterKey + " (in object: " + template.id() + ")");
                return false;
            }
            if (!dataObject.value.containsKey(parameterKey) && value.get(parameterKey) != null) {
                //System.out.println("Non-matching object parameter data (parameter only present in current data): " + parameterKey + " (in object: " + template.id() + ")");
                return false;
            }
            if (!Objects.equals(value.get(parameterKey), dataObject.value.get(parameterKey))) {
                //System.out.println("Non-matching object parameter data (parameters are present but have different values): " + parameterKey + " (in object: " + template.id() + ")");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        if (!(data instanceof DataObject dataObject)) {
            return false;
        }
        if (!Objects.equals(getTemplate(), dataObject.getTemplate())) {
            return false;
        }
        String primaryParameter = template.nameFormat();
        if (primaryParameter == null) {
            return false;
        }
        Data primaryParameterData = value.get(primaryParameter);
        Data comparisonPrimaryParameterData = dataObject.value.get(primaryParameter);
        return primaryParameterData.isDuplicateValue(comparisonPrimaryParameterData);
    }

    @Override
    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Object: ");
        sb.append(template.id());
        sb.append(" {");
        boolean isFirst = true;
        for (Map.Entry<String, Data> entry : value.entrySet()) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue() == null ? null : entry.getValue().getDebugString());
        }
        sb.append("}");
        return sb.toString();
    }

}
