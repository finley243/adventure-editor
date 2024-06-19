package com.github.finley243.adventureeditor.template;

public record ComponentOption(String id, String name, String object) {

    @Override
    public String toString() {
        return name;
    }

}
