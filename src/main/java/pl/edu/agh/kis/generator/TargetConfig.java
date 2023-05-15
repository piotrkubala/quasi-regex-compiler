package pl.edu.agh.kis.generator;

import pl.edu.agh.kis.validation.Validator;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration of output specific to particular programming language.
 */
public class TargetConfig {
    private final String templateFile;
    private Map<List<String>, Integer> counterConfig;
    private Map<String, List<String>> includes;
    private Validator validator;
    private String name;

    public TargetConfig(String file) {
        templateFile = file;
        counterConfig = new HashMap<>();
        includes = new HashMap<>();
    }

    public TargetConfig counterConfig(Map<List<String>, Integer> config) {
        this.counterConfig = config;
        return this;
    }

    public TargetConfig includes(Map<String, List<String>> includes) {
        this.includes = includes;
        return this;
    }

    public TargetConfig validator(Validator validator) {
        this.validator = validator;
        return this;
    }

    public TargetConfig name(String name) {
        this.name = name;
        return this;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public Map<List<String>, Integer> getCounterConfig() {
        return counterConfig;
    }

    public Map<String, List<String>> getIncludes() {
        return includes;
    }

    public Validator getValidator() {
        return validator;
    }

    public String getName() {
        if (name == null || name.isEmpty())
            return Paths.get(templateFile).getFileName().toString().replaceAll("[.].*", "").toUpperCase();
        else return name;
    }
}
