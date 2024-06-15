package com.github.finley243.adventureeditor.data;

import java.util.Objects;

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

}
