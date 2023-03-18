package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

public class PatternToJavaTranslator extends PatternTranslator{
    PatternGrammarVisitorToJavaTranslator visitor = new PatternGrammarVisitorToJavaTranslator();

    public PatternToJavaTranslator(String textToParse) {
        super(textToParse);
    }

    public String generateCode() {
        System.out.println(visitor.visitPattern(patternRoot).toString());

        System.out.println("---===CLASS===---\n");

        return visitor.toString();
    }
}
