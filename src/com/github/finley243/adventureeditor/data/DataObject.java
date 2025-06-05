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

        // Handle binary flag patterns: {flag:param:trueStr:falseStr} or {flag:param:trueStr}
        nameString = processFlagPatterns(nameString);
        // Handle non-null value injection: {value:param:default} or {value:param}
        nameString = processValuePatterns(nameString);

        List<String> parameterKeys = new ArrayList<>(value.keySet());
        parameterKeys.sort(Comparator.comparingInt(String::length));
        for (String parameterKey : parameterKeys) {
            Data parameterValue = value.get(parameterKey);
            String parameterString = parameterValue == null ? "null" : parameterValue.toString();
            nameString = nameString.replace("$" + parameterKey, parameterString);
        }
        return nameString;
    }

    /**
     * Replaces all {flag:param:trueStr:falseStr} or {flag:param:trueStr} patterns in the input string.
     * Supports both parenthesized and non-parenthesized true/false strings.
     */
    private String processFlagPatterns(String input) {
        // Match {flag:param:trueStr:falseStr} or {flag:param:trueStr}
        String pattern = "\\{flag:([a-zA-Z0-9_]+):([^:{}]+)(?::([^:{}]+))?}";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String param = matcher.group(1);
            String trueStr = matcher.group(2);
            String falseStr = matcher.group(3); // may be null
            Data paramData = value.get(param);
            boolean isTrue = paramData instanceof DataBoolean && ((DataBoolean) paramData).getValue();
            String replacement = isTrue ? trueStr : (falseStr != null ? falseStr : "");
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Replaces all {value:param:prefix:suffix:default}, {value:param:prefix:suffix}, {value:param:prefix}, {value:param:default}, or {value:param} patterns in the input string.
     * If the parameter is non-null, injects prefix + toString value + suffix. If null, injects the default if provided, else nothing.
     */
    private String processValuePatterns(String input) {
        // Regex: {value:param:prefix:suffix:default} (all optional except param)
        String pattern = "\\{value:([a-zA-Z0-9_]+)(?::([^:{}]*))?(?::([^:{}]*))?(?::([^:{}]*))?}";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String param = matcher.group(1);
            String prefix = matcher.group(2);
            String suffix = matcher.group(3);
            String defaultStr = matcher.group(4);
            Data paramData = value.get(param);
            String replacement;
            if (paramData != null) {
                String valueStr = paramData.toString();
                replacement = (prefix != null ? prefix : "") + valueStr + (suffix != null ? suffix : "");
            } else {
                replacement = (defaultStr != null ? defaultStr : "");
            }
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
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
