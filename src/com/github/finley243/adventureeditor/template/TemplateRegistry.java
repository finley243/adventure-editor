package com.github.finley243.adventureeditor.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRegistry {

    private static final String CONFIG_TEMPLATE_ID = "config";

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

    public Template getConfigTemplate() {
        return templateMap.get(CONFIG_TEMPLATE_ID);
    }

    public List<String> getEnumValues(String ID) {
        return new ArrayList<>(enumMap.get(ID));
    }

    public String[] getEnumValuesArray(String ID) {
        return enumMap.get(ID).toArray(new String[0]);
    }

    public Map<String, Template> getTemplatesForComponents(List<ComponentOption> componentOptions) {
        Map<String, Template> templatesForIDs = new HashMap<>();
        for (ComponentOption option : componentOptions) {
            String templateID = option.object();
            templatesForIDs.put(templateID, getTemplate(templateID));
        }
        return templatesForIDs;
    }

}
