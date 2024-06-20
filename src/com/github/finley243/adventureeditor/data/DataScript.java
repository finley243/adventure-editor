package com.github.finley243.adventureeditor.data;

import java.util.Objects;

public class DataScript extends Data {

    private final String value;

    public DataScript(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataScript(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataScript dataString && Objects.equals(dataString.value, value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataScript dataString && Objects.equals(dataString.value, value);
    }

}
