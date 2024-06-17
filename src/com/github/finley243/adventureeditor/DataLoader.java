package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
import com.github.finley243.adventureeditor.template.Group;
import com.github.finley243.adventureeditor.template.TabGroup;
import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataLoader {

    public static void loadTemplates(File dir, Map<String, Template> templates, Map<String, Set<String>> enumTypes) throws ParserConfigurationException, IOException, SAXException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    for (Element enumTypeElement : LoadUtils.directChildrenWithName(rootElement, "enumType")) {
                        String id = LoadUtils.attribute(enumTypeElement, "id", null);
                        Set<String> values = LoadUtils.setOfTags(enumTypeElement, "value");
                        enumTypes.put(id, values);
                    }
                    for (Element templateElement : LoadUtils.directChildrenWithName(rootElement, "template")) {
                        String id = LoadUtils.attribute(templateElement, "id", null);
                        String name = LoadUtils.attribute(templateElement, "name", null);
                        boolean topLevel = LoadUtils.attributeBool(templateElement, "topLevel", false);
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
                                case "stringSet" -> TemplateParameter.ParameterDataType.STRING_SET;
                                case "object" -> TemplateParameter.ParameterDataType.OBJECT;
                                case "objectSet" -> TemplateParameter.ParameterDataType.OBJECT_SET;
                                case "reference" -> TemplateParameter.ParameterDataType.REFERENCE;
                                case "enum" -> TemplateParameter.ParameterDataType.ENUM;
                                case "script" -> TemplateParameter.ParameterDataType.SCRIPT;
                                case null, default -> throw new IllegalArgumentException("Invalid parameter data type: " + dataTypeString);
                            };
                            String parameterID = LoadUtils.attribute(parameterElement, "id", null);
                            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
                            String type = LoadUtils.attribute(parameterElement, "type", null);
                            boolean topLevelOnly = LoadUtils.attributeBool(parameterElement, "topLevelOnly", false);
                            boolean optional = LoadUtils.attributeBool(parameterElement, "optional", false);
                            TemplateParameter.ParameterFormat format = LoadUtils.attributeEnum(parameterElement, "format", TemplateParameter.ParameterFormat.class, TemplateParameter.ParameterFormat.CHILD_TAG);
                            String group = LoadUtils.attribute(parameterElement, "group", null);
                            int x = LoadUtils.attributeInt(parameterElement, "x", 0);
                            int y = LoadUtils.attributeInt(parameterElement, "y", 0);
                            int width = LoadUtils.attributeInt(parameterElement, "width", 1);
                            int height = LoadUtils.attributeInt(parameterElement, "height", 1);
                            parameters.add(new TemplateParameter(parameterID, dataType, parameterName, type, topLevelOnly, optional, format, group, x, y, width, height));
                        }
                        String primaryParameter = LoadUtils.attribute(templateElement, "primaryParameter", null);
                        Template template = new Template(id, name, topLevel, groups, tabGroups, parameters, primaryParameter);
                        templates.put(id, template);
                    }
                }
            }
        }
    }

    public static Map<String, Map<String, Data>> loadFromDir(File dir, Map<String, Template> templates) throws ParserConfigurationException, IOException, SAXException {
        if (dir.isDirectory()) {
            Map<String, Map<String, Data>> dataMap = new HashMap<>();
            File[] files = dir.listFiles();
            assert files != null;
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
                            if (template != null) {
                                DataObject data = loadDataFromElement(currentElement, template, templates);
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
        return null;
    }

    private static DataObject loadDataFromElement(Element element, Template template, Map<String, Template> templates) {
        Map<String, Data> dataMap = new HashMap<>();
        for (TemplateParameter parameter : template.parameters()) {
            switch (parameter.dataType()) {
                case BOOLEAN -> {
                    Boolean value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeBool(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagBoolean(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataBoolean(value));
                    }
                }
                case INTEGER -> {
                    Integer value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeInt(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagInt(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataInteger(value));
                    }
                }
                case FLOAT -> {
                    Float value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attributeFloat(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTagFloat(element, parameter.id(), null);
                        default -> null;
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataFloat(value));
                    }
                }
                case STRING -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> element == null ? null : element.getTextContent();
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataString(value));
                    }
                }
                case STRING_SET -> dataMap.put(parameter.id(), new DataStringSet(LoadUtils.listOfTags(element, parameter.id())));
                case OBJECT -> {
                    Element objectElement = LoadUtils.singleChildWithName(element, parameter.id());
                    if (objectElement == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        Data objectData = loadDataFromElement(LoadUtils.singleChildWithName(element, parameter.id()), templates.get(parameter.type()), templates);
                        dataMap.put(parameter.id(), objectData);
                    }
                }
                case OBJECT_SET -> {
                    List<Data> objectList = new ArrayList<>();
                    for (Element objectElement : LoadUtils.directChildrenWithName(element, parameter.id())) {
                        Data objectData = loadDataFromElement(objectElement, templates.get(parameter.type()), templates);
                        objectList.add(objectData);
                    }
                    dataMap.put(parameter.id(), new DataObjectSet(objectList));
                }
                case REFERENCE -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> element == null ? null : element.getTextContent();
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataReference(value));
                    }
                }
                case ENUM -> {
                    String value = switch (parameter.format()) {
                        case ATTRIBUTE -> LoadUtils.attribute(element, parameter.id(), null);
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> element == null ? null : element.getTextContent();
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataEnum(value));
                    }
                }
                case SCRIPT -> {
                    String value = switch (parameter.format()) {
                        case CHILD_TAG -> LoadUtils.singleTag(element, parameter.id(), null);
                        case CURRENT_TAG -> element == null ? null : element.getTextContent();
                        default -> null;
                    };
                    if (value == null) {
                        dataMap.put(parameter.id(), null);
                    } else {
                        dataMap.put(parameter.id(), new DataScript(value));
                    }
                }
            }
        }
        return new DataObject(template, dataMap);
    }

}
