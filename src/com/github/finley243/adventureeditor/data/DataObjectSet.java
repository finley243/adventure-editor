package com.github.finley243.adventureeditor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DataObjectSet extends Data {

    private final List<Data> value;
    private final List<UUID> ids;

    public DataObjectSet(List<Data> value) {
        this(value, generateFreshIDs(value.size()));
    }

    public DataObjectSet(List<Data> value, List<UUID> ids) {
        this.value = value;
        this.ids = ids;
    }

    public List<Data> getValue() {
        return value;
    }

    public List<UUID> getIds() {
        return ids;
    }

    @Override
    public Data createCopy() {
        // Do not copy UUIDs
        List<Data> copyList = new ArrayList<>();
        for (Data data : value) {
            copyList.add(data.createCopy());
        }
        return new DataObjectSet(copyList);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        // Do not check UUIDs here (they are for internal use only, and have no bearing on data equality)
        return o instanceof DataObjectSet dataObjectSet && Objects.equals(value, dataObjectSet.value);
    }

    @Override
    public boolean isDuplicateValue(Data data) {
        if (!(data instanceof DataObjectSet dataObjectSet)) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        //Template template = ((DataObject) value.getFirst()).getTemplate();
        for (Data innerData : value) {
            boolean hasDuplicate = false;
            for (Data otherInnerData : dataObjectSet.value) {
                if (innerData.isDuplicateValue(otherInnerData)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (!hasDuplicate) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Object Set: [");
        boolean isFirst = true;
        for (Data data : value) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append(data.getDebugString());
        }
        sb.append("]");
        return sb.toString();
    }

    private static List<UUID> generateFreshIDs(int count) {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(UUID.randomUUID());
        }
        return ids;
    }

}
