package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class PatternToJavaTranslator extends PatternTranslator{
    ParseTreeWalker walker = new ParseTreeWalker();
    PatternGrammarListenerToJavaTranslator listener = new PatternGrammarListenerToJavaTranslator();

    public PatternToJavaTranslator(String textToParse) {
        super(textToParse);
    }

    public String generateCode() {
        String output = "";

        ParseTreeWalker.DEFAULT.walk(listener, patternRoot);

        return output;
    }
}
