package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataLoader {

    private static final String TEMPLATE_DIRECTORY = "templates";
    private static final String RECENT_PROJECTS_FILE = "recents.txt";

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_EXTENSION = "xml";
    private static final String SCRIPT_DIRECTORY = "data/scripts";
    private static final String SCRIPT_EXTENSION = "ascr";
    private static final String CONFIG_FILE = "config.xml";
    private static final String PHRASE_FILE = "phrases.aphr";
    private static final String PHRASE_DIRECTORY = "data";
    private static final String PHRASE_EXTENSION = "aphr";

    private static final String TOP_LEVEL_ELEMENT_NAME = "data";
    private static final String COMPONENT_TYPE_ATTRIBUTE_ID = "type";

    private final DocumentBuilder documentBuilder;
    private final Transformer transformer;

    public DataLoader() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DataIOException("Failed to create document builder");
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            this.transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new DataIOException("Failed to create XML transformer");
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    public Map<String, Template> loadTemplates() {
        Map<String, Template> templates = new HashMap<>();
        File dir = new File(TEMPLATE_DIRECTORY);
        if (!dir.isDirectory()) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (!file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase(DATA_EXTENSION)) continue;
            Element rootElement = getRootElementFromFile(file);
            for (Element templateElement : LoadUtils.directChildrenWithName(rootElement, "template")) {
                String id = LoadUtils.attribute(templateElement, "id", null);
                String name = LoadUtils.attribute(templateElement, "name", null);
                boolean topLevel = LoadUtils.attributeBool(templateElement, "topLevel", false);
                boolean isUnique = LoadUtils.attributeBool(templateElement, "unique", topLevel);
                List<TabGroup> tabGroups = new ArrayList<>();
                for (Element tabGroupElement : LoadUtils.directChildrenWithName(templateElement, "tabGroup")) {
                    String groupID = LoadUtils.attribute(tabGroupElement, "id", null);
                    String groupName = LoadUtils.attribute(tabGroupElement, "name", null);
                    int x = LoadUtils.attributeInt(tabGroupElement, "x", 0);
                    int y = LoadUtils.attributeInt(tabGroupElement, "y", 0);
                    int width = LoadUtils.attributeInt(tabGroupElement, "width", 1);
                    int height = LoadUtils.attributeInt(tabGroupElement, "height", 1);
                    tabGroups.add(new TabGroup(groupID, groupName, x, y, width, height));
                }
                List<Group> groups = new ArrayList<>();
                for (Element groupElement : LoadUtils.directChildrenWithName(templateElement, "group")) {
                    String groupID = LoadUtils.attribute(groupElement, "id", null);
                    String groupName = LoadUtils.attribute(groupElement, "name", null);
                    String tabGroup = LoadUtils.attribute(groupElement, "tabGroup", null);
                    int x = LoadUtils.attributeInt(groupElement, "x", 0);
                    int y = LoadUtils.attributeInt(groupElement, "y", 0);
                    int width = LoadUtils.attributeInt(groupElement, "width", 1);
                    int height = LoadUtils.attributeInt(groupElement, "height", 1);
                    groups.add(new Group(groupID, groupName, tabGroup, x, y, width, height));
                }
                List<TemplateParameter> parameters = new ArrayList<>();
                for (Element parameterElement : LoadUtils.directChildrenWithName(templateElement, "parameter")) {
                    String dataTypeString = LoadUtils.attribute(parameterElement, "dataType", null);
                    TemplateParameter.ParameterDataType dataType = switch (dataTypeString) {
                        case "boolean" -> TemplateParameter.ParameterDataType.BOOLEAN;
                        case "integer" -> TemplateParameter.ParameterDataType.INTEGER;
                        case "float" -> TemplateParameter.ParameterDataType.FLOAT;
                        case "string" -> TemplateParameter.ParameterDataType.STRING;
                        case "stringLong" -> TemplateParameter.ParameterDataType.STRING_LONG;
                        case "object" -> TemplateParameter.ParameterDataType.OBJECT;
                        case "objectSet" -> TemplateParameter.ParameterDataType.OBJECT_SET;
                        case "objectSetUnique" -> TemplateParameter.ParameterDataType.OBJECT_SET_UNIQUE;
                        case "reference" -> TemplateParameter.ParameterDataType.REFERENCE;
                        case "referenceSet" -> TemplateParameter.ParameterDataType.REFERENCE_SET;
                        case "enum" -> TemplateParameter.ParameterDataType.ENUM;
                        case "script" -> TemplateParameter.ParameterDataType.SCRIPT;
                        case "component" -> TemplateParameter.ParameterDataType.COMPONENT;
                        case "tree" -> TemplateParameter.ParameterDataType.TREE;
                        case "treeBranch" -> TemplateParameter.ParameterDataType.TREE_BRANCH;
                        case null, default -> throw new IllegalArgumentException("Invalid parameter data type in template " + id + ": " + dataTypeString);
                    };
                    String parameterID = LoadUtils.attribute(parameterElement, "id", null);
                    String parameterName = LoadUtils.attribute(parameterElement, "name", null);
                    String type = LoadUtils.attribute(parameterElement, "type", null);
                    boolean topLevelOnly = LoadUtils.attributeBool(parameterElement, "topLevelOnly", false);
                    boolean optional = LoadUtils.attributeBool(parameterElement, "optional", false);
                    TemplateParameter.ParameterFormat format = LoadUtils.attributeEnum(parameterElement, "format", TemplateParameter.ParameterFormat.class, TemplateParameter.ParameterFormat.CHILD_TAG);
                    String componentFormatString = LoadUtils.attribute(parameterElement, "componentFormat", null);
                    TemplateParameter.ComponentFormat componentFormat = switch (componentFormatString) {
                        case "typeAttribute" -> TemplateParameter.ComponentFormat.TYPE_ATTRIBUTE;
                        case "textOrTags" -> TemplateParameter.ComponentFormat.TEXT_OR_TAGS;
                        case null, default -> null;
                    };
                    List<ComponentOption> componentOptions = new ArrayList<>();
                    for (Element componentOptionElement : LoadUtils.directChildrenWithName(parameterElement, "component")) {
                        String optionID = LoadUtils.attribute(componentOptionElement, "id", null);
                        String optionName = LoadUtils.attribute(componentOptionElement, "name", null);
                        String optionObject = LoadUtils.attribute(componentOptionElement, "object", null);
                        componentOptions.add(new ComponentOption(optionID, optionName, optionObject));
                    }
                    boolean useComponentTypeName = LoadUtils.attributeBool(parameterElement, "useComponentTypeName", false);
                    String group = LoadUtils.attribute(parameterElement, "group", null);
                    int x = LoadUtils.attributeInt(parameterElement, "x", 0);
                    int y = LoadUtils.attributeInt(parameterElement, "y", 0);
                    int width = LoadUtils.attributeInt(parameterElement, "width", 1);
                    int height = LoadUtils.attributeInt(parameterElement, "height", 1);
                    Data defaultValue = null;
                    String defaultValueString = LoadUtils.attribute(parameterElement, "default", null);
                    if (defaultValueString != null) {
                        defaultValue = switch (dataType) {
                            case BOOLEAN -> new DataBoolean(Boolean.parseBoolean(defaultValueString));
                            case INTEGER -> new DataInteger(Integer.parseInt(defaultValueString));
                            case FLOAT -> new DataFloat(Float.parseFloat(defaultValueString));
                            case STRING, STRING_LONG -> new DataString(defaultValueString);
                            case REFERENCE -> new DataReference(defaultValueString);
                            case ENUM -> new DataEnum(defaultValueString);
                            case SCRIPT -> new DataScript(defaultValueString);
                            case COMPONENT -> new DataComponent(defaultValueString, null, null);
                            case OBJECT, OBJECT_SET_UNIQUE, OBJECT_SET, REFERENCE_SET, TREE, TREE_BRANCH -> null;
                        };
                    }
                    parameters.add(new TemplateParameter(parameterID, dataType, parameterName, type, topLevelOnly, optional, format, componentFormat, componentOptions, useComponentTypeName, group, x, y, width, height, defaultValue));
                }
                String nameFormat = LoadUtils.attribute(templateElement, "nameFormat", null);
                String primaryParameter = LoadUtils.attribute(templateElement, "primaryParameter", null);
                Template template = new Template(id, name, topLevel, isUnique, groups, tabGroups, parameters, nameFormat, primaryParameter);
                templates.put(id, template);
            }
        }
        return templates;
    }

    public Map<String, List<String>> loadEnumTypes() {
        Map<String, List<String>> enumTypes = new HashMap<>();
        File dir = new File(TEMPLATE_DIRECTORY);
        if (!dir.isDirectory()) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (!file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) continue;
            Element rootElement = getRootElementFromFile(file);
            for (Element enumTypeElement : LoadUtils.directChildrenWithName(rootElement, "enumType")) {
                String id = LoadUtils.attribute(enumTypeElement, "id", null);
                List<String> values = LoadUtils.listOfTags(enumTypeElement, "value");
                enumTypes.put(id, values);
            }
        }
        return enumTypes;
    }

    public List<ProjectFile> loadRecentProjects() {
        List<ProjectFile> recentProjects = new ArrayList<>();
        File file = new File(RECENT_PROJECTS_FILE);
        if (!file.exists()) {
            return recentProjects;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    recentProjects.add(new ProjectFile(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            throw new DataIOException("Failed to load recent projects file");
        }
        return recentProjects;
    }

    public void saveRecentProjects(List<ProjectFile> recentProjects) {
        File file = new File(RECENT_PROJECTS_FILE);
        try (FileWriter writer = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            StringBuilder builder = new StringBuilder();
            for (ProjectFile project : recentProjects) {
                builder.append(project.name()).append("|").append(project.absolutePath()).append("\n");
            }
            String content = builder.toString();
            bufferedWriter.write(content);
        } catch (IOException e) {
            throw new DataIOException("Failed to save recent projects file");
        }
    }

    public ProjectLoadData loadFromDir(File projectDir, TemplateRegistry templateRegistry) {
        if (!projectDir.isDirectory()) {
            throw new IllegalArgumentException("Selected file is not a directory");
        }
        Data configData = loadConfigDataFromDir(projectDir, templateRegistry.getConfigTemplate(), templateRegistry);
        Map<String, Map<String, Data>> gameData = loadDataFromDir(projectDir, templateRegistry);
        Map<String, String> phrases = loadPhrasesFromDir(projectDir);
        Map<String, String> scripts = loadScriptsFromDir(projectDir);
        return new ProjectLoadData(configData, gameData, phrases, scripts);
    }

    private Map<String, Map<String, Data>> loadDataFromDir(File projectDir, TemplateRegistry templateRegistry) {
        Map<String, Map<String, Data>> dataMap = new HashMap<>();
        File dataDirectory = new File(projectDir, DATA_DIRECTORY);
        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
            return dataMap;
        }
        File[] files = dataDirectory.listFiles();
        if (files == null) {
            return dataMap;
        }
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(DATA_EXTENSION)) {
                Element rootElement = getRootElementFromFile(file);
                Node currentChild = rootElement.getFirstChild();
                while (currentChild != null) {
                    if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                        Element currentElement = (Element) currentChild;
                        String elementType = currentChild.getNodeName();
                        Template template = templateRegistry.getTemplate(elementType);
                        if (template != null && template.topLevel()) {
                            DataObject data = loadDataFromElement(currentElement, template, templateRegistry, true, dataMap);
                            if (!dataMap.containsKey(elementType)) {
                                dataMap.put(elementType, new HashMap<>());
                            }
                            String dataID = data.getID();
                            if (dataID != null) {
                                dataMap.get(elementType).put(data.getID(), data);
                            }
                        }
                    }
                    currentChild = currentChild.getNextSibling();
                }
            }
        }
        return dataMap;
    }

    private Map<String, String> loadPhrasesFromDir(File projectDir) {
        Map<String, String> phrases = new HashMap<>();
        File phraseDirectory = new File(projectDir, PHRASE_DIRECTORY);
        if (!phraseDirectory.exists() || !phraseDirectory.isDirectory()) {
            return phrases;
        }
        File[] files = phraseDirectory.listFiles();
        if (files == null) {
            return phrases;
        }
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(PHRASE_EXTENSION)) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] split = line.split(":", 2);
                        if (split.length != 2) throw new DataIOException("Invalid phrase file format - line: " + line);
                        phrases.put(split[0].trim(), split[1].trim());
                    }
                } catch (FileNotFoundException e) {
                    throw new DataIOException("Scanner could not find file while loading phrase file: " + file.getAbsolutePath());
                }
            }
        }
        return phrases;
    }

    private Map<String, String> loadScriptsFromDir(File projectDir) {
        Map<String, String> scripts = new HashMap<>();
        File scriptDirectory = new File(projectDir, SCRIPT_DIRECTORY);
        if (!scriptDirectory.exists() || !scriptDirectory.isDirectory()) {
            return scripts;
        }
        File[] files = scriptDirectory.listFiles();
        if (files == null) {
            return scripts;
        }
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(SCRIPT_EXTENSION)) {
                String scriptName = file.getName().substring(0, file.getName().lastIndexOf("."));
                String scriptBody;
                try {
                    scriptBody = Files.readString(file.toPath());
                } catch (IOException e) {
                    throw new DataIOException("Failed to read script file: " + file.getAbsolutePath());
                }
                scripts.put(scriptName, scriptBody);
            }
        }
        return scripts;
    }

    public void saveToDir(File dir, TemplateRegistry templateRegistry, Map<String, Map<String, Data>> dataMap, ConfigMenuManager configMenuManager, Map<String, String> scripts, Map<String, String> phrases) {
        if (dir.isDirectory()) {
            saveConfigData(dir, templateRegistry.getConfigTemplate(), configMenuManager, dataMap);
            File dataDirectory = new File(dir, DATA_DIRECTORY);
            dataDirectory.mkdirs();
            File scriptDirectory = new File(dir, SCRIPT_DIRECTORY);
            scriptDirectory.mkdirs();

            // Delete all existing game files in the directory
            Path dirPath = Paths.get(dataDirectory.getAbsolutePath());
            Path scriptDirPath = Paths.get(scriptDirectory.getAbsolutePath());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*." + DATA_EXTENSION)) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new DataIOException("Failed to delete existing game data files while saving");
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(scriptDirPath, "*." + SCRIPT_EXTENSION)) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new DataIOException("Failed to delete existing script files while saving");
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*." + PHRASE_EXTENSION)) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                throw new DataIOException("Failed to delete existing phrase files while saving");
            }

            for (Map.Entry<String, Map<String, Data>> entry : dataMap.entrySet()) {
                String categoryID = entry.getKey();
                Template categoryTemplate = templateRegistry.getTemplate(categoryID);
                if (!categoryTemplate.topLevel()) {
                    continue;
                }
                Map<String, Data> categoryData = entry.getValue();
                // TODO - Switch to dedicated file name stored in Data (loaded from templates)
                File categoryFile = new File(dataDirectory, categoryID + "." + DATA_EXTENSION);
                try {
                    categoryFile.createNewFile();
                } catch (IOException e) {
                    throw new DataIOException("Failed to create data file for type: " + categoryID);
                }
                saveDataToFile(categoryData, categoryFile, dataMap);
            }

            File phraseFile = new File(dataDirectory, PHRASE_FILE);
            try {
                phraseFile.createNewFile();
            } catch (IOException e) {
                throw new DataIOException("Failed to create phrase file");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(phraseFile))) {
                for (Map.Entry<String, String> phrase : phrases.entrySet()) {
                    writer.write(phrase.getKey() + ":" + phrase.getValue());
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new DataIOException("Failed to write phrase file");
            }

            for (Map.Entry<String, String> script : scripts.entrySet()) {
                File scriptFile = new File(scriptDirectory, script.getKey() + "." + SCRIPT_EXTENSION);
                try {
                    scriptFile.createNewFile();
                } catch (IOException e) {
                    throw new DataIOException("Failed to create script file");
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
                    writer.write(script.getValue());
                } catch (IOException e) {
                    throw new DataIOException("Failed to write to script file: " + scriptFile.getAbsolutePath());
                }
            }
        }
    }

    private Data loadConfigDataFromDir(File dir, Template configTemplate, TemplateRegistry templateRegistry) {
        File configFile = new File(dir, CONFIG_FILE);
        if (!configFile.exists()) {
            return null;
        }
        Element rootElement = getRootElementFromFile(configFile);
        if (rootElement == null) {
            return null;
        }
        return loadDataFromElement(rootElement, configTemplate, templateRegistry, true, new HashMap<>());
    }

    private void saveConfigData(File dir, Template configTemplate, ConfigMenuManager configMenuManager, Map<String, Map<String, Data>> globalDataMap) {
        File configFile = new File(dir, CONFIG_FILE);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new DataIOException("Failed to create new config file: " + configFile.getAbsolutePath());
            }
        }
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement(TOP_LEVEL_ELEMENT_NAME);
        document.appendChild(rootElement);
        DataObject objectData = (DataObject) configMenuManager.getConfigData();
        addObjectToElement(objectData, rootElement, document, globalDataMap);
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(configFile);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new DataIOException("An error has occurred during transformer operation while saving config: " + e.getMessage());
        }
    }

    private DataObject loadDataFromElement(Element element, Template template, TemplateRegistry templateRegistry, boolean isTopLevel, Map<String, Map<String, Data>> globalDataMap) {
        if (element == null) {
            return null;
        }
        Map<String, Data> objectDataMap = new HashMap<>();
        for (TemplateParameter parameter : template.parameters()) {
            Data defaultValueOrNull = (parameter.topLevelOnly() && !isTopLevel) || parameter.optional() ? null : parameter.defaultValue();
            switch (parameter.dataType()) {
                case BOOLEAN -> {
                    Boolean value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeBool(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagBoolean(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataBoolean(value));
                    }
                }
                case INTEGER -> {
                    Integer value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeInt(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagInt(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataInteger(value));
                    }
                }
                case FLOAT -> {
                    Float value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeFloat(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagFloat(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataFloat(value));
                    }
                }
                case STRING, STRING_LONG -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> LoadUtils.textContent(element, null);
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataString(value));
                    }
                }
                case OBJECT -> {
                    Element objectElement;
                    if (parameter.format() == TemplateParameter.ParameterFormat.CURRENT_TAG) {
                        objectElement = element;
                    } else {
                        objectElement = LoadUtils.singleChildWithName(element, parameter.id());
                    }
                    if (objectElement == null) {
                        objectDataMap.put(parameter.id(), null);
                    } else {
                        Template objectTemplate = templateRegistry.getTemplate(parameter.type());
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templateRegistry, false, globalDataMap);
                        if (!objectTemplate.topLevel() && objectTemplate.unique()) {
                            String objectType = parameter.type();
                            if (!globalDataMap.containsKey(objectType)) {
                                globalDataMap.put(objectType, new HashMap<>());
                            }
                            String objectID = objectData.getID();
                            if (objectID != null) {
                                globalDataMap.get(objectType).put(objectID, objectData);
                            }
                        }
                        objectDataMap.put(parameter.id(), objectData);
                    }
                }
                case OBJECT_SET, OBJECT_SET_UNIQUE -> {
                    List<Data> objectList = new ArrayList<>();
                    Template objectTemplate = templateRegistry.getTemplate(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templateRegistry, false, globalDataMap);
                        if (!objectTemplate.topLevel() && objectTemplate.unique()) {
                            String objectType = parameter.type();
                            if (!globalDataMap.containsKey(objectType)) {
                                globalDataMap.put(objectType, new HashMap<>());
                            }
                            String objectID = objectData.getID();
                            if (objectID != null) {
                                globalDataMap.get(objectType).put(objectID, objectData);
                            }
                        }
                        objectList.add(objectData);
                    }
                    objectDataMap.put(parameter.id(), new DataObjectSet(objectList));
                }
                case REFERENCE_SET -> {
                    List<String> referenceList = new ArrayList<>();
                    Template objectTemplate = templateRegistry.getTemplate(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templateRegistry, false, globalDataMap);
                        String objectID = objectData.getID();
                        if (!objectTemplate.topLevel() && objectTemplate.unique()) {
                            String objectType = parameter.type();
                            if (!globalDataMap.containsKey(objectType)) {
                                globalDataMap.put(objectType, new HashMap<>());
                            }
                            if (objectID != null) {
                                globalDataMap.get(objectType).put(objectID, objectData);
                            }
                        }
                        referenceList.add(objectID);
                    }
                    objectDataMap.put(parameter.id(), new DataReferenceSet(referenceList));
                }
                case REFERENCE -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> LoadUtils.textContent(element, null);
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataReference(value));
                    }
                }
                case ENUM -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> LoadUtils.textContent(element, null);
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataEnum(value));
                    }
                }
                case SCRIPT -> {
                    String value = switch (parameter.format()) {
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> LoadUtils.textContent(element, null);
                        default -> null;
                    };
                    if (value == null) {
                        objectDataMap.put(parameter.id(), defaultValueOrNull);
                    } else {
                        objectDataMap.put(parameter.id(), new DataScript(value));
                    }
                }
                case COMPONENT -> {
                    String componentType = switch (parameter.componentFormat()) {
                        case TYPE_ATTRIBUTE -> LoadUtils.attribute(element, COMPONENT_TYPE_ATTRIBUTE_ID, parameter.defaultValue() == null ? null : ((DataComponent) parameter.defaultValue()).getType());
                        case TEXT_OR_TAGS -> LoadUtils.hasTextContent(element) ? "text" : "tags";
                    };
                    if (componentType == null) {
                        objectDataMap.put(parameter.id(), null);
                    } else {
                        Map<String, ComponentOption> optionsMap = new HashMap<>();
                        for (ComponentOption option : parameter.componentOptions()) {
                            optionsMap.put(option.id(), option);
                        }
                        Data objectData = loadDataFromElement(element, templateRegistry.getTemplate(optionsMap.get(componentType).object()), templateRegistry, false, globalDataMap);
                        String nameOverride = parameter.useComponentTypeName() ? optionsMap.get(componentType).name() : null;
                        objectDataMap.put(parameter.id(), new DataComponent(componentType, objectData, nameOverride));
                    }
                }
                case TREE -> {
                    List<Data> topNodes = new ArrayList<>();
                    Template objectTemplate = templateRegistry.getTemplate(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templateRegistry, false, globalDataMap);
                        topNodes.add(objectData);
                    }
                    objectDataMap.put(parameter.id(), new DataTree(topNodes));
                }
                case TREE_BRANCH -> {
                    List<Data> topNodes = new ArrayList<>();
                    Template objectTemplate = templateRegistry.getTemplate(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templateRegistry, false, globalDataMap);
                        topNodes.add(objectData);
                    }
                    objectDataMap.put(parameter.id(), new DataTreeBranch(topNodes));
                }
            }
        }
        return new DataObject(template, objectDataMap);
    }

    private void saveDataToFile(Map<String, Data> data, File file, Map<String, Map<String, Data>> globalDataMap) {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement(TOP_LEVEL_ELEMENT_NAME);
        document.appendChild(rootElement);
        for (Data currentData : data.values()) {
            DataObject objectData = (DataObject) currentData;
            if (objectData != null) {
                Element objectElement = document.createElement(objectData.getTemplate().id());
                addObjectToElement(objectData, objectElement, document, globalDataMap);
                rootElement.appendChild(objectElement);
            }
        }
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new DataIOException("An error has occurred during transformer operation while saving game data: " + e.getMessage());
        }
    }

    private void addObjectToElement(DataObject objectData, Element objectElement, Document document, Map<String, Map<String, Data>> globalDataMap) {
        if (objectData == null) {
            return;
        }
        for (TemplateParameter parameter : objectData.getTemplate().parameters()) {
            Data parameterData = objectData.getValue().get(parameter.id());
            if (parameterData == null /*&& parameter.optional()*/) {
                continue;
            }
            switch (parameter.dataType()) {
                case BOOLEAN -> {
                    String value = Boolean.toString(((DataBoolean) parameterData).getValue());
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                    }
                }
                case INTEGER -> {
                    String value = Integer.toString(((DataInteger) parameterData).getValue());
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                    }
                }
                case FLOAT -> {
                    String value = Float.toString(((DataFloat) parameterData).getValue());
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                    }
                }
                case STRING, STRING_LONG -> {
                    String value = ((DataString) parameterData).getValue();
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                        case CURRENT_TAG -> objectElement.setTextContent(value);
                    }
                }
                case OBJECT -> {
                    Element childElement;
                    if (parameter.format() == TemplateParameter.ParameterFormat.CURRENT_TAG) {
                        childElement = objectElement;
                    } else {
                        childElement = document.createElement(parameter.id());
                    }
                    addObjectToElement((DataObject) parameterData, childElement, document, globalDataMap);
                    if (parameter.format() != TemplateParameter.ParameterFormat.CURRENT_TAG) {
                        objectElement.appendChild(childElement);
                    }
                }
                case OBJECT_SET, OBJECT_SET_UNIQUE -> {
                    List<Data> values = ((DataObjectSet) parameterData).getValue();
                    for (Data value : values) {
                        Element childElement = document.createElement(parameter.id());
                        addObjectToElement((DataObject) value, childElement, document, globalDataMap);
                        objectElement.appendChild(childElement);
                    }
                }
                case REFERENCE_SET -> {
                    List<String> values = ((DataReferenceSet) parameterData).getValue();
                    for (String referenceID : values) {
                        Data value = globalDataMap.get(parameter.type()).get(referenceID);
                        Element childElement = document.createElement(parameter.id());
                        addObjectToElement((DataObject) value, childElement, document, globalDataMap);
                        objectElement.appendChild(childElement);
                    }
                }
                case REFERENCE -> {
                    String value = ((DataReference) parameterData).getValue();
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                        case CURRENT_TAG -> objectElement.setTextContent(value);
                    }
                }
                case ENUM -> {
                    String value = ((DataEnum) parameterData).getValue();
                    switch (parameter.format()) {
                        case ATTRIBUTE -> objectElement.setAttribute(parameter.id(), value);
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                        case CURRENT_TAG -> objectElement.setTextContent(value);
                    }
                }
                case SCRIPT -> {
                    String value = ((DataScript) parameterData).getValue();
                    switch (parameter.format()) {
                        case CHILD_TAG -> {
                            Element childElement = document.createElement(parameter.id());
                            childElement.setTextContent(value);
                            objectElement.appendChild(childElement);
                        }
                        case CURRENT_TAG -> objectElement.setTextContent(value);
                    }
                }
                case COMPONENT -> {
                    String componentType = ((DataComponent) parameterData).getType();
                    DataObject componentObjectData = (DataObject) ((DataComponent) parameterData).getObjectData();
                    if (parameter.componentFormat() == TemplateParameter.ComponentFormat.TYPE_ATTRIBUTE && componentType != null) {
                        objectElement.setAttribute(COMPONENT_TYPE_ATTRIBUTE_ID, componentType);
                    }
                    addObjectToElement(componentObjectData, objectElement, document, globalDataMap);
                }
                case TREE -> {
                    List<Data> values = ((DataTree) parameterData).getValue();
                    for (Data value : values) {
                        Element childElement = document.createElement(parameter.id());
                        addObjectToElement((DataObject) value, childElement, document, globalDataMap);
                        objectElement.appendChild(childElement);
                    }
                }
                case TREE_BRANCH -> {
                    List<Data> values = ((DataTreeBranch) parameterData).getValue();
                    for (Data value : values) {
                        Element childElement = document.createElement(parameter.id());
                        addObjectToElement((DataObject) value, childElement, document, globalDataMap);
                        objectElement.appendChild(childElement);
                    }
                }
            }
        }
    }

    private Element getRootElementFromFile(File file) {
        Document document;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException | IOException e) {
            throw new DataIOException("Failed to create document for file: " + file.getAbsolutePath());
        }
        return document.getDocumentElement();
    }

}
