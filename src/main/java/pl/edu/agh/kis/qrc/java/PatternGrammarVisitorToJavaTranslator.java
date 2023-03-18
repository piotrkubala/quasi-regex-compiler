package pl.edu.agh.kis.qrc.java;

import pl.edu.agh.kis.qrc.PatternGrammarBaseVisitor;
import pl.edu.agh.kis.qrc.PatternGrammarParser;

import java.util.*;

public class PatternGrammarVisitorToJavaTranslator extends PatternGrammarBaseVisitor<JavaProgramCode> {
    private final List<String> errors = new ArrayList<>();

    private final JavaProgramClass generatedClass = new JavaProgramClass();

    private List<JavaProgramCode> getNArgumentsForPattern(PatternGrammarParser.ArgumentsContext ctx, int n) {
        List<JavaProgramCode> ans = new ArrayList<>();

        ans.add(visitPattern(ctx.pattern()));
        for (int i = 0; i < n - 1; i++) {
            ctx = ctx.args_with_delim.arguments;
            ans.add(visitPattern(ctx.pattern()));
        }

        return ans;
    }

    private JavaProgramCode createLoop(PatternGrammarParser.PatternContext ctx) {
        List<JavaProgramCode> loopArguments = getNArgumentsForPattern(ctx.arguments, 4);

        JavaProgramCode code = loopArguments.get(0);
        JavaProgramCode conditionCode = loopArguments.get(1);
        JavaProgramCode bodyCode = loopArguments.get(2);
        JavaProgramCode followingCode = loopArguments.get(3);

        code.appendToLastLineOfCode(";");
        code.appendLineOfCode("while (", 0);
        code.addCodeAsBooleanFunction(conditionCode);
        code.appendToLastLineOfCode(") {");
        code.appendCode(bodyCode, 1);
        code.appendToLastLineOfCode(";");
        code.appendLineOfCode("}", -1);
        code.appendCode(followingCode, 0);
        code.appendToLastLineOfCode(";");

        return code;
    }

    private JavaProgramCode createCond(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = new JavaProgramCode(generatedClass);



        return code;
    }

    private JavaProgramCode createAtom(PatternGrammarParser.PatternContext ctx) {
        JavaProgramCode code = new JavaProgramCode(generatedClass);

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

    public JavaProgramClass getGeneratedClass() {
        return generatedClass;
    }

    public String toString() {
        return generatedClass.toString();
    }
}