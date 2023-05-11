package pl.edu.agh.kis;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import pl.edu.agh.kis.generator.Generator;
import pl.edu.agh.kis.generator.TargetConfig;
import pl.edu.agh.kis.model.Analyser;
import pl.edu.agh.kis.model.Model;
import pl.edu.agh.kis.validation.DefaultValidator;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Compiler {
    public enum Language {
        JAVA (new TargetConfig("src/main/templates/java.stg")
                .counterConfig(Map.ofEntries(
                        entry(List.of("Concur", "Para"), 2)
                ))
                .validator(new DefaultValidator())
        ),
        PYTHON (new TargetConfig("src/main/templates/python.stg")
                .counterConfig(Map.ofEntries(
                        entry(List.of("Concur", "Para"), 2)
                ))
                .includes(Map.ofEntries(
                        entry("Concur", List.of("thread")),
                        entry("Para", List.of("thread"))
                ))
                .validator(new DefaultValidator())
        );

        Language(TargetConfig config) {
            this.config = config;
        }

        public final TargetConfig config;
    }


    public static String compile(String pattern, TargetConfig config) {
        CharStream stream = CharStreams.fromString(pattern);
        PatternLexer lexer = new PatternLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PatternParser parser = new PatternParser(tokens);
        Model model = Analyser.analyse(parser);

        if (config.getValidator() != null)
            config.getValidator().validate(model);
        return Generator.generate(model, config);
    }

    public static String compile(String pattern, Language language) {
        return compile(pattern, language.config);
    }
}
