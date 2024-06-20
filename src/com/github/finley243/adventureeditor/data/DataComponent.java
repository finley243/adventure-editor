package com.github.finley243.adventureeditor.data;

import java.util.Objects;

public class DataComponent extends Data {

    private final String type;
    private final Data objectData;
    private final String nameOverride;

    public DataComponent(String type, Data objectData, String nameOverride) {
        this.type = type;
        this.objectData = objectData;
        this.nameOverride = nameOverride;
    }

    public String getType() {
        return type;
    }

    public Data getObjectData() {
        return objectData;
    }

    @Override
    public Data createCopy() {
        return new DataComponent(type, objectData.createCopy(), nameOverride);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataComponent dataComponent && Objects.equals(type, dataComponent.type) && Objects.equals(objectData, dataComponent.objectData);
    }

    @Override
    public String toString() {
        if (nameOverride != null) {
            return nameOverride;
        }
        return objectData.toString();
    }

}
