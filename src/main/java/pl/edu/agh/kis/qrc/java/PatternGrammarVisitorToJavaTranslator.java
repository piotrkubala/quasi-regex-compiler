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
        code.appendLineOfCode("while (", 0);
        code.addCodeAsBooleanFunction(conditionCode);
        code.appendToLastLineOfCode(") {");
        code.appendCode(bodyCode, 1);
        code.appendLineOfCode("}", -1);
        code.appendCode(followingCode, 0);

        return code;
    }

    private JavaProgramCode createCond(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = new JavaProgramCode();



        return code;
    }

    private JavaProgramCode createAtom(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = new JavaProgramCode();

        String functionCallName = ctx.getText() + "()";

        code.createNewEmptyFunctionIfNotExistsAndAppendCall(functionCallName);

        return code;
    }

    /**
     * @param ctx the parse tree
     * @return list of lines of the parsed pattern
     */
    @Override
    public JavaProgramCode visitPattern(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = null;
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
                code = createCond(ctx);
                break;
            case "Para":
                break;
            case "Loop":
                code = createLoop(ctx);
                break;
            case "Choice":
                break;
            case "SeqSeq":
                break;
            case "Repeat":
                break;
            default:
                code = createAtom(ctx);
        }

        return code;
    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }
}