package com.github.finley243.adventureeditor.data;

import java.util.Objects;

public class DataReference extends Data {

    private final String value;

    public DataReference(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataReference(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataReference dataReference && Objects.equals(dataReference.value, value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataReference dataReference && Objects.equals(dataReference.value, value);
    }

}
