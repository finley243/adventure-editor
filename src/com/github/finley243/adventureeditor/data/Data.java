package com.github.finley243.adventureeditor.data;

public abstract class Data {

    public abstract Data createCopy();

    public abstract boolean isDuplicateValue(Data data);

}
