package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import pl.edu.agh.kis.qrc.PatternGrammarParser;
import pl.edu.agh.kis.qrc.PatternGrammarLexer;

public abstract class PatternTranslator {
    PatternGrammarLexer patternLexer;
    CommonTokenStream tokens;
    PatternGrammarParser patternParser;

    PatternGrammarParser.PatternContext patternRoot;

    public PatternTranslator(String textToParse) {
        patternLexer = new PatternGrammarLexer(CharStreams.fromString(textToParse));
        tokens = new CommonTokenStream(patternLexer);
        patternParser = new PatternGrammarParser(tokens);
        patternRoot = patternParser.pattern();
    }
}
