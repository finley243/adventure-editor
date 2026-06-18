package com.github.finley243.adventureeditor.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRegistry {

    private final Map<String, Template> templateMap;
    private final Map<String, List<String>> enumMap;

    public TemplateRegistry(Map<String, Template> templateMap, Map<String, List<String>> enumMap) {
        this.templateMap = new HashMap<>(templateMap);
        this.enumMap = new HashMap<>(enumMap);
    }

    public Template getTemplate(String ID) {
        return templateMap.get(ID);
    }

    public Map<String, Template> getAllTemplates() {
        return new HashMap<>(templateMap);
    }

    public List<String> getEnumValues(String ID) {
        return new ArrayList<>(enumMap.get(ID));
    }

}
