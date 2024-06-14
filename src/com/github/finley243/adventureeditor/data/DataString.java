package com.github.finley243.adventureeditor.data;

public class DataString extends Data {

    private final String value;

    public DataString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
