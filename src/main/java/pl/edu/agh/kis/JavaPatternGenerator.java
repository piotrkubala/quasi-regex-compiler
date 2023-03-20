package pl.edu.agh.kis;

import java.util.HashSet;
import java.util.Set;

public class JavaPatternGenerator extends PatternGenerator {

    private final Program program;
    private final Set<String> functions;
    private final Set<String> predicates;

    public JavaPatternGenerator() {
        this.program = new Program();
        this.functions = new HashSet<>();
        this.predicates = new HashSet<>();
    }

    @Override
    public Program generate(PatternParser.PatternContext ctx) {
        Program main = visitPattern(ctx);
        Program definitions = new Program();

        for (String predicate : predicates) {
            definitions.appendLine("private static boolean " + predicate + "() {")
                    .appendLine("return true;", 1)
                    .appendLine("}\n");
        }

        for (String function : functions) {
            definitions.appendLine("private static void " + function + "() {")
                    .appendLine("", 1)
                    .appendLine("}\n");
        }

        return program.appendLine("public class Program {")
                .appendBlock(definitions, 1)
                .appendLine("public static void main(String[] args) {", 1)
                .appendBlock(main, 2)
                .appendLine("}", 1)
                .appendLine("}");
    }

    @Override
    protected String visitAtom(String name) {
        functions.add(name);
        return name + "();";
    }

    @Override
    protected String visitPredicate(String name) {
        predicates.add(name);
        return name + "()";
    }

    @Override
    protected Program visitSeq(Program first, Program then) {
        return new Program().appendBlock(first).appendBlock(then);
    }

    @Override
    protected Program visitBranch(String predicate, Program thenBranch, Program elseBranch) {
        return new Program().appendLine("if (" + predicate + ") {")
                .appendBlock(thenBranch, 1)
                .appendLine("} else {")
                .appendBlock(elseBranch, 1)
                .appendLine("}");
    }

    @Override
    protected Program visitConcur(Program preStatement, Program firstThread, Program secondThread) {
        // TO DO
        return new Program().appendBlock(preStatement).appendBlock(firstThread).appendBlock(secondThread);
    }

    @Override
    protected Program visitCond(String predicate, Program thenBranch, Program elseBranch, Program postStatement) {
        return new Program().appendLine("if (" + predicate + ") {")
                .appendBlock(thenBranch, 1)
                .appendLine("} else {")
                .appendBlock(elseBranch, 1)
                .appendLine("}")
                .appendBlock(postStatement);
    }

    @Override
    protected Program visitPara(Program preStatement, Program firstThread, Program secondThread, Program postStatement) {
        // TO DO
        return new Program().appendBlock(preStatement).appendBlock(firstThread).appendBlock(secondThread).appendBlock(postStatement);
    }

    @Override
    protected Program visitLoop(Program preStatement, String predicate, Program loopBody, Program postStatement) {
        return new Program().appendBlock(preStatement)
                .appendLine("while (" + predicate + ") {")
                .appendBlock(loopBody, 1)
                .appendLine("}")
                .appendBlock(postStatement);
    }

    @Override
    protected Program visitSeqSeq(Program first, Program then, Program last) {
        return new Program().appendBlock(first).appendBlock(then).appendBlock(last);
    }

    @Override
    protected Program visitRepeat(Program preStatement, Program loopBody, String predicate, Program postStatement) {
        return new Program().appendBlock(preStatement)
                .appendLine("do {")
                .appendBlock(loopBody, 1)
                .appendLine("} while (!" + predicate + ");")
                .appendBlock(postStatement);
    }
}
