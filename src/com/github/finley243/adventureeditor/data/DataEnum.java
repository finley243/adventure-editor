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
    public String toString() {
        return "ENUM: " + value;
    }

}
