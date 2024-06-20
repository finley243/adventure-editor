package com.github.finley243.adventureeditor.data;

public class DataFloat extends Data {

    private final float value;

    public DataFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataFloat(value);
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataFloat dataFloat && dataFloat.value == value;
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataFloat dataFloat && dataFloat.value == value;
    }

}
