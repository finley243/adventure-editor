package com.github.finley243.adventureeditor.data;

import java.util.*;

public class DataReferenceSet extends Data {

    private final List<String> value;

    public DataReferenceSet(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }

    @Override
    public Data createCopy() {
        List<String> copyList = new ArrayList<>(value);
        return new DataReferenceSet(copyList);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataReferenceSet dataObjectSet && Objects.equals(value, dataObjectSet.value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        if (!(data instanceof DataReferenceSet dataReferenceSet)) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        Set<String> valueSet = new HashSet<>(value);
        Set<String> otherValueSet = new HashSet<>(dataReferenceSet.value);
        return Objects.equals(valueSet, otherValueSet);
    }

    @Override
    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reference Set: [");
        boolean isFirst = true;
        for (String reference : value) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append(reference);
        }
        sb.append("]");
        return sb.toString();
    }

}
