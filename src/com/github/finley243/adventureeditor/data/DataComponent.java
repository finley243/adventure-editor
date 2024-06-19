package com.github.finley243.adventureeditor.data;

public class DataComponent extends Data {

    private final String type;
    private final Data objectData;

    public DataComponent(String type, Data objectData) {
        this.type = type;
        this.objectData = objectData;
    }

    public String getType() {
        return type;
    }

    public Data getObjectData() {
        return objectData;
    }

    @Override
    public Data createCopy() {
        return new DataComponent(type, objectData.createCopy());
    }

    @Override
    public String toString() {
        return objectData.toString();
    }

}
