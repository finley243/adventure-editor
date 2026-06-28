package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.data.Data;

import java.util.Map;

public record ProjectLoadData(Data configData, Map<String, Map<String, Data>> gameData, Map<String, String> phrases, Map<String, String> scripts) {
}
