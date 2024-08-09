package com.github.finley243.adventureeditor.data;

import java.util.Objects;

public class DataEnum extends Data {

    private final String value;

    public DataEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        return new DataEnum(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataEnum dataEnum && Objects.equals(dataEnum.value, value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        return data instanceof DataEnum dataEnum && Objects.equals(dataEnum.value, value);
    }

    @Override
    public String getDebugString() {
        return "Enum(" + value + ")";
    }

}
