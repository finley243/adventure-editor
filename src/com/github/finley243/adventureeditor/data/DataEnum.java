package com.github.finley243.adventureeditor.data;

public class DataEnum extends Data {

    private final String value;

    public DataEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataEnum(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
