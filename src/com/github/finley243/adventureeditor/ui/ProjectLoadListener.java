package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.data.Data;
import com.github.finley243.adventureeditor.template.Template;

import java.util.Map;

public interface ProjectLoadListener {

    void onLoadProject(Map<String, Template> templates, Map<String, Map<String, Data>> loadedData);

}
