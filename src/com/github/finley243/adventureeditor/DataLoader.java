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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

    private static final String DATA_DIRECTORY = "/data";
    private static final String CONFIG_FILE = "/config.xml";

    private static final String COMPONENT_TYPE_ATTRIBUTE_ID = "type";

    public static void loadTemplates(Map<String, Template> templates, Map<String, List<String>> enumTypes) throws ParserConfigurationException, IOException, SAXException {
        File dir = new File(TEMPLATE_DIRECTORY);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    for (Element enumTypeElement : LoadUtils.directChildrenWithName(rootElement, "enumType")) {
                        String id = LoadUtils.attribute(enumTypeElement, "id", null);
                        List<String> values = LoadUtils.listOfTags(enumTypeElement, "value");
                        enumTypes.put(id, values);
                    }
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
                                    case OBJECT, OBJECT_SET_UNIQUE, OBJECT_SET, REFERENCE_SET -> null;
                                };
                            }
                            parameters.add(new TemplateParameter(parameterID, dataType, parameterName, type, topLevelOnly, optional, format, componentFormat, componentOptions, useComponentTypeName, group, x, y, width, height, defaultValue));
                        }
                        String primaryParameter = LoadUtils.attribute(templateElement, "primaryParameter", null);
                        Template template = new Template(id, name, topLevel, isUnique, groups, tabGroups, parameters, primaryParameter);
                        templates.put(id, template);
                    }
                }
            }
        }
    }

    public static void loadRecentProjects(List<ProjectData> recentProjects) {
        File file = new File(RECENT_PROJECTS_FILE);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    recentProjects.add(new ProjectData(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRecentProjects(List<ProjectData> recentProjects) {
        File file = new File(RECENT_PROJECTS_FILE);
        try (FileWriter writer = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            file.createNewFile();
            StringBuilder builder = new StringBuilder();
            for (ProjectData project : recentProjects) {
                builder.append(project.name()).append("|").append(project.absolutePath()).append("\n");
            }
            String content = builder.toString();
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromDir(File dir, Map<String, Template> templates, Map<String, Map<String, Data>> dataMap, ConfigMenuManager configMenuManager, Map<String, String> scripts, Map<String, String> phrases) throws ParserConfigurationException, IOException, SAXException {
        if (dir.isDirectory()) {
            loadConfigData(dir, templates.get(ConfigMenuManager.CONFIG_TEMPLATE), configMenuManager);
            File dataDirectory = new File(dir, DATA_DIRECTORY);
            if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
                return;
            }
            File[] files = dataDirectory.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    Node currentChild = rootElement.getFirstChild();
                    while (currentChild != null) {
                        if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element currentElement = (Element) currentChild;
                            String elementType = currentChild.getNodeName();
                            Template template = templates.get(elementType);
                            if (template != null && template.topLevel()) {
                                DataObject data = loadDataFromElement(currentElement, template, templates, true, dataMap);
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
                } else if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("ascr")) {
                    String scriptName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    String scriptPath = file.getAbsolutePath();
                    scripts.put(scriptName, scriptPath);
                } else if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("aphr")) {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] split = line.split(":");
                        if (split.length != 2) throw new UnsupportedOperationException("Invalid phrase file format - line: " + line);
                        phrases.put(split[0].trim(), split[1].trim());
                    }
                    scanner.close();
                }
            }
        }
    }

    public static void saveToDir(File dir, Map<String, Template> templates, Map<String, Map<String, Data>> dataMap, ConfigMenuManager configMenuManager, Map<String, String> phrases) throws IOException, TransformerException, ParserConfigurationException {
        if (dir.isDirectory()) {
            saveConfigData(dir, templates.get(ConfigMenuManager.CONFIG_TEMPLATE), configMenuManager, dataMap);
            File dataDirectory = new File(dir, DATA_DIRECTORY);
            dataDirectory.mkdirs();

            // Delete all existing game files in the directory
            Path dirPath = Paths.get(dataDirectory.getAbsolutePath());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.xml")) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            }
            /*try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.ascr")) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            }*/
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.aphr")) {
                for (Path path : stream) {
                    Files.delete(path);
                }
            }

            for (Map.Entry<String, Map<String, Data>> entry : dataMap.entrySet()) {
                String categoryID = entry.getKey();
                Template categoryTemplate = templates.get(categoryID);
                if (!categoryTemplate.topLevel()) {
                    continue;
                }
                Map<String, Data> categoryData = entry.getValue();
                File categoryFile = new File(dataDirectory, categoryID + ".xml");
                categoryFile.createNewFile();
                saveDataToFile(categoryData, categoryFile, templates, dataMap);
            }

            File phraseFile = new File(dataDirectory, "phrases.aphr");
            phraseFile.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(phraseFile))) {
                for (Map.Entry<String, String> phrase : phrases.entrySet()) {
                    writer.write(phrase.getKey() + ":" + phrase.getValue());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadConfigData(File dir, Template configTemplate, ConfigMenuManager configMenuManager) throws ParserConfigurationException, IOException, SAXException {
        File configFile = new File(dir, CONFIG_FILE);
        if (!configFile.exists()) {
            return;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(configFile);
        Element rootElement = document.getDocumentElement();
        if (rootElement == null) {
            return;
        }
        Data configData = loadDataFromElement(rootElement, configTemplate, new HashMap<>(), true, new HashMap<>());
        configMenuManager.setConfigData(configData);
    }

    private static void saveConfigData(File dir, Template configTemplate, ConfigMenuManager configMenuManager, Map<String, Map<String, Data>> globalDataMap) throws IOException, ParserConfigurationException, TransformerException {
        File configFile = new File(dir, CONFIG_FILE);
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement("data");
        document.appendChild(rootElement);
        DataObject objectData = (DataObject) configMenuManager.getConfigData();
        addObjectToElement(objectData, rootElement, document, globalDataMap);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(configFile);
        transformer.transform(source, result);
    }

    private static DataObject loadDataFromElement(Element element, Template template, Map<String, Template> templates, boolean isTopLevel, Map<String, Map<String, Data>> globalDataMap) {
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
                        Template objectTemplate = templates.get(parameter.type());
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templates, false, globalDataMap);
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
                    Template objectTemplate = templates.get(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templates, false, globalDataMap);
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
                    Template objectTemplate = templates.get(parameter.type());
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        DataObject objectData = loadDataFromElement(objectElement, objectTemplate, templates, false, globalDataMap);
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
                        Data objectData = loadDataFromElement(element, templates.get(optionsMap.get(componentType).object()), templates, false, globalDataMap);
                        String nameOverride = parameter.useComponentTypeName() ? optionsMap.get(componentType).name() : null;
                        objectDataMap.put(parameter.id(), new DataComponent(componentType, objectData, nameOverride));
                    }
                }
            }
        }
        return new DataObject(template, objectDataMap);
    }

    private static void saveDataToFile(Map<String, Data> data, File file, Map<String, Template> templates, Map<String, Map<String, Data>> globalDataMap) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement("data");
        document.appendChild(rootElement);
        for (Data currentData : data.values()) {
            DataObject objectData = (DataObject) currentData;
            if (objectData != null) {
                Element objectElement = document.createElement(objectData.getTemplate().id());
                addObjectToElement(objectData, objectElement, document, globalDataMap);
                rootElement.appendChild(objectElement);
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static void addObjectToElement(DataObject objectData, Element objectElement, Document document, Map<String, Map<String, Data>> globalDataMap) {
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
                    String value = ((DataBoolean) parameterData).getValue() ? "true" : "false";
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
                    //Element childElement = document.createElement(parameter.id());
                    Element childElement;
                    if (parameter.format() == TemplateParameter.ParameterFormat.CURRENT_TAG) {
                        childElement = objectElement;
                    } else {
                        childElement = document.createElement(parameter.id());
                    }
                    addObjectToElement((DataObject) parameterData, childElement, document, globalDataMap);
                    objectElement.appendChild(childElement);
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
                    //Element childElement = document.createElement(parameter.id());
                    if (parameter.componentFormat() == TemplateParameter.ComponentFormat.TYPE_ATTRIBUTE && componentType != null) {
                        objectElement.setAttribute(COMPONENT_TYPE_ATTRIBUTE_ID, componentType);
                    }
                    addObjectToElement(componentObjectData, objectElement, document, globalDataMap);
                    //objectElement.appendChild(childElement);
                }
            }
        }
    }

}
