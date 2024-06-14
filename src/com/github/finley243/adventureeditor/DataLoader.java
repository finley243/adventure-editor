package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {

    public static Map<String, Template> loadTemplates(File dir) throws ParserConfigurationException, IOException, SAXException {
        Map<String, Template> templates = new HashMap<>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    for (Element templateElement : LoadUtils.directChildrenWithName(rootElement, "template")) {
                        String id = LoadUtils.attribute(templateElement, "id", null);
                        String name = LoadUtils.attribute(templateElement, "name", null);
                        boolean topLevel = LoadUtils.attributeBool(templateElement, "topLevel", false);
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
                                case null, default -> throw new IllegalArgumentException("Invalid parameter data type: " + dataTypeString);
                            };
                            String parameterID = LoadUtils.attribute(parameterElement, "id", null);
                            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
                            String type = LoadUtils.attribute(parameterElement, "type", null);
                            List<String> enumOptions = LoadUtils.listOfTags(parameterElement, "value");
                            boolean topLevelOnly = LoadUtils.attributeBool(parameterElement, "topLevelOnly", false);
                            boolean optional = LoadUtils.attributeBool(parameterElement, "optional", false);
                            TemplateParameter.ParameterFormat format = LoadUtils.attributeEnum(parameterElement, "format", TemplateParameter.ParameterFormat.class, TemplateParameter.ParameterFormat.CHILD_TAG);
                            parameters.add(new TemplateParameter(parameterID, dataType, parameterName, type, enumOptions, topLevelOnly, optional, format));
                        }
                        String primaryParameter = LoadUtils.attribute(templateElement, "primaryParameter", null);
                        Template template = new Template(id, name, topLevel, parameters, primaryParameter);
                        templates.put(id, template);
                    }
                }
            }
        }
        return templates;
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
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataBoolean(LoadUtils.attributeBool(element, parameter.id(), false)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataBoolean(LoadUtils.singleTagBoolean(element, parameter.id(), false)));
                    }
                }
                case INTEGER -> {
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataInteger(LoadUtils.attributeInt(element, parameter.id(), 0)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataInteger(LoadUtils.singleTagInt(element, parameter.id(), 0)));
                    }
                }
                case FLOAT -> {
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataFloat(LoadUtils.attributeFloat(element, parameter.id(), 0.0f)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataFloat(LoadUtils.singleTagFloat(element, parameter.id(), 0.0f)));
                    }
                }
                case STRING -> {
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataString(LoadUtils.attribute(element, parameter.id(), null)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataString(LoadUtils.singleTag(element, parameter.id(), null)));
                        case CURRENT_TAG -> dataMap.put(parameter.id(), new DataString(element == null ? null : element.getTextContent()));
                    }
                }
                case STRING_SET -> dataMap.put(parameter.id(), new DataStringSet(LoadUtils.listOfTags(element, parameter.id())));
                case OBJECT -> {
                    Data objectData = loadDataFromElement(LoadUtils.singleChildWithName(element, parameter.id()), templates.get(parameter.type()), templates);
                    dataMap.put(parameter.id(), objectData);
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
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataReference(LoadUtils.attribute(element, parameter.id(), null)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataReference(LoadUtils.singleTag(element, parameter.id(), null)));
                        case CURRENT_TAG -> dataMap.put(parameter.id(), new DataReference(element == null ? null : element.getTextContent()));
                    }
                }
                case ENUM -> {
                    switch (parameter.format()) {
                        case ATTRIBUTE -> dataMap.put(parameter.id(), new DataEnum(LoadUtils.attribute(element, parameter.id(), null)));
                        case CHILD_TAG -> dataMap.put(parameter.id(), new DataEnum(LoadUtils.singleTag(element, parameter.id(), null)));
                        case CURRENT_TAG -> dataMap.put(parameter.id(), new DataEnum(element == null ? null : element.getTextContent()));
                    }
                }
            }
        }
        return new DataObject(template, dataMap);
    }

}
