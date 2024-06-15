package com.github.finley243.adventureeditor.data;

public class DataReference extends Data {

    private final String value;

    public DataReference(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataReference(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
