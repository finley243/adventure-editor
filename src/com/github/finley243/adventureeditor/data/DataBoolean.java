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
    public Data createCopy() {
        return new DataBoolean(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataBoolean dataBoolean && dataBoolean.value == value;
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataBoolean dataBoolean && dataBoolean.value == value;
    }

}
