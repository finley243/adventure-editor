package com.github.finley243.adventureeditor.data;

public class DataInteger extends Data {

    private final int value;

    public DataInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataInteger(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataInteger dataInteger && dataInteger.value == value;
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataInteger dataInteger && dataInteger.value == value;
    }

    @Override
    public String getDebugString() {
        return Integer.toString(value);
    }

}
