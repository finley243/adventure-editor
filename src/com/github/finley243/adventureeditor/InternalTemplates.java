package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.template.Template;
import com.github.finley243.adventureeditor.template.TemplateParameter;

import java.util.ArrayList;

public class InternalTemplates {

    public static final Template SCRIPT_TEMPLATE = new Template("script", "Script", false, null, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("script", TemplateParameter.ParameterDataType.SCRIPT, null, null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null, null);

    public static final Template PHRASE_TEMPLATE = new Template("phrase", "Phrase", false, null, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() {{
        add(new TemplateParameter("key", TemplateParameter.ParameterDataType.STRING, "Key", null, false, false, null, null, new ArrayList<>(), false, null, 0, 0, 1, 1, null));
        add(new TemplateParameter("text", TemplateParameter.ParameterDataType.STRING_LONG, "Phrase", null, false, false, null, null, new ArrayList<>(), false, null, 0, 1, 1, 1, null));
    }}, null, null);

}
