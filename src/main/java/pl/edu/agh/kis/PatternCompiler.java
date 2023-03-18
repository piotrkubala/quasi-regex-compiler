package pl.edu.agh.kis;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class PatternCompiler {

    public enum Language {
        JAVA,
        PYTHON
    }

    public static String compile(String pattern, Language language) {
        CharStream stream = CharStreams.fromString(pattern);
        PatternLexer lexer = new PatternLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PatternParser parser = new PatternParser(tokens);

        PatternGenerator generator = switch (language) {
            case JAVA -> new JavaPatternGenerator();
            case PYTHON -> new PythonPatternGenerator();
        };

        Program program = generator.generate(parser.pattern());

        return program.build();
    }
}
