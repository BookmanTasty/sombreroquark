package com.leyvadev.sombreroquark.utils;

import com.leyvadev.sombreroquark.model.SombreroUser;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class EmailTemplateParser {

    public List<String> getTemplateVariables(String template, String... exclude) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        variables.removeIf(variable -> {
            for (String s : exclude) {
                if (variable.contains(s)) {
                    return true;
                }
            }
            return false;
        });
        return variables;
    }

    public Map<String, String> getVariableValuesFromSombreroUserAsMap(String template, SombreroUser user) {
        List<String> variables = getTemplateVariables(template, "data");
        Map<String, String> values = new HashMap<>();
        for (String variable : variables) {
            if (variable.contains(".")) {
                String[] split = variable.split("\\.");
                try {
                    values.put(variable, user.getDataAsMap().get(split[1]).toString());
                } catch (NullPointerException e) {
                    values.put(variable, "");
                }
            } else {
                if (variable.equals("username")) {
                    values.put(variable, user.getUsername());
                } else if (variable.equals("email")) {
                    values.put(variable, user.getEmail());
                } else {
                    values.put(variable, "");
                }
            }
        }
        return values;
    }

    public String parseTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
