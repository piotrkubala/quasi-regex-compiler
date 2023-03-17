package pl.edu.agh.kis.qrc.java;

import org.antlr.v4.runtime.misc.NotNull;
import pl.edu.agh.kis.qrc.PatternGrammarBaseListener;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

import java.util.*;

public class PatternGrammarListenerToJavaTranslator extends PatternGrammarBaseListener {
    private List<String> errors = new ArrayList<>();

    private JavaProgramCode javaCode = new JavaProgramCode();

    @Override
    public void enterPattern(PatternGrammarParser.PatternContext ctx) {
        String patternName = ctx.children.get(0).getText();

        switch(patternName) {
            case "Seq":
                break;
            case "Branch":
                break;
            case "BranchRe":
                break;
            case "Concur":
                break;
            case "ConcurRe":
                break;
            case "Cond":
                break;
            case "Para":
                break;
            case "Loop":

                break;
            case "Choice":
                break;
            case "SeqSeq":
                break;
            case "Repeat":
                break;
        }
    }

    @Override
    public void exitPattern(PatternGrammarParser.PatternContext ctx) {

    }

    @Override
    public void enterArguments(PatternGrammarParser.ArgumentsContext ctx) {

    }

    @Override
    public void exitArguments(PatternGrammarParser.ArgumentsContext ctx) {

    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }

    public String getJavaCode() {
        return javaCode.toString();
    }
}