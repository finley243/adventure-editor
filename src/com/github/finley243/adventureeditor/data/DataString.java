package com.github.finley243.adventureeditor.data;

import java.util.Objects;

public class DataString extends Data {

    private final String value;

    public DataString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataString(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataString dataString && Objects.equals(dataString.value, value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataString dataString && Objects.equals(dataString.value, value);
    }

    @Override
    public String getDebugString() {
        return "String(" + value + ")";
    }

}
