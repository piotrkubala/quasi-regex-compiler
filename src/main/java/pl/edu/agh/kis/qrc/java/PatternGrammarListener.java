package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.tree.TerminalNode;
import pl.edu.agh.kis.qrc.PatternGrammarBaseListener;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatternGrammarListener extends PatternGrammarBaseListener {
    private List<String> errors = new ArrayList<>();

    @Override
    public void enterPattern(PatternGrammarParser.PatternContext ctx) {
        String patternName = ctx.getText();

        System.out.println("Pattern name is: " + patternName);
    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }
}