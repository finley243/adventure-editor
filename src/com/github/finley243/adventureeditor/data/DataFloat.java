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
    public String toString() {
        return Float.toString(value);
    }

}
