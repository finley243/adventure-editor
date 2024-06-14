package com.github.finley243.adventureeditor.data;

public class DataBoolean extends Data {

    private final boolean value;

    public DataBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

}
