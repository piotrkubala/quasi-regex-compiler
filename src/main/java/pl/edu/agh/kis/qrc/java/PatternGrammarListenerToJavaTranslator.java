package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.misc.NotNull;
import pl.edu.agh.kis.qrc.PatternGrammarBaseListener;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatternGrammarListenerToJavaTranslator extends PatternGrammarBaseListener {
    private List<String> errors = new ArrayList<>();

    @Override
    public void enterPattern(PatternGrammarParser.PatternContext ctx) {
        String patternName = ctx.getText();

        System.out.println("Pattern name is: " + patternName);
    }

    @Override
    public void exitPattern(PatternGrammarParser.PatternContext ctx) {

    }

    @Override
    public void enterArguments(PatternGrammarParser.ArgumentsContext ctx) {

    }

    @Override
    public void exitArguments(@NotNull PatternGrammarParser.ArgumentsContext ctx) {

    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }
}