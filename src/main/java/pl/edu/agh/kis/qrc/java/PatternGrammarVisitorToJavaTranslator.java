package pl.edu.agh.kis.qrc.java;

import pl.edu.agh.kis.qrc.PatternGrammarBaseVisitor;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

import java.util.*;

public class PatternGrammarVisitorToJavaTranslator extends PatternGrammarBaseVisitor<JavaProgramCode> {
    private List<String> errors = new ArrayList<>();

    private JavaProgramCode createLoop(PatternGrammarParser.PatternContext ctx) {
        PatternGrammarParser.ArgumentsContext firstArg = ctx.arguments;
        PatternGrammarParser.ArgumentsContext secondArg = firstArg.args_with_delim.arguments;
        PatternGrammarParser.ArgumentsContext thirdArg = secondArg.args_with_delim.arguments;
        PatternGrammarParser.ArgumentsContext fourthArg = thirdArg.args_with_delim.arguments;

        PatternGrammarParser.PatternContext firstPattern = firstArg.pattern();
        PatternGrammarParser.PatternContext secondPattern = secondArg.pattern();
        PatternGrammarParser.PatternContext thirdPattern = thirdArg.pattern();
        PatternGrammarParser.PatternContext fourthPattern = fourthArg.pattern();

        JavaProgramCode previousCode = visitPattern(firstPattern);
        JavaProgramCode conditionCode = visitPattern(secondPattern);
        JavaProgramCode bodyCode = visitPattern(thirdPattern);
        JavaProgramCode followingCode = visitPattern(fourthPattern);

        JavaProgramCode code = previousCode;
        code.AppendLineOfCode("while (", 0);


        return code;
    }

    private JavaProgramCode createCond(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = new JavaProgramCode();



        return code;
    }

    /**
     * @param ctx the parse tree
     * @return list of lines of the parsed pattern
     */
    @Override
    public JavaProgramCode visitPattern(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code;
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
            default:
                throw new RuntimeException("Unknown pattern");
        }
    }

    @Override
    public JavaProgramCode visitArguments(PatternGrammarParser.ArgumentsContext ctx) {

    }

    @Override
    public JavaProgramCode visitArgs_with_delim(PatternGrammarParser.Args_with_delimContext ctx) {

    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }
}