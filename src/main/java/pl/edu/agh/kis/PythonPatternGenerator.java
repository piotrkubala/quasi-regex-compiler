package pl.edu.agh.kis;

import java.util.HashSet;
import java.util.Set;

public class PythonPatternGenerator extends PatternGenerator {

    private final Program program;
    private final Set<String> functions;
    private final Set<String> predicates;
    private int tasksToParallelise = 0;

    public PythonPatternGenerator() {
        this.program = new Program();
        this.functions = new HashSet<>();
        this.predicates = new HashSet<>();
    }

    @Override
    public Program generate(PatternParser.PatternContext ctx) {
        Program main = visitPattern(ctx);
        Program definitions = new Program();

        for (String predicate : predicates) {
            definitions.appendLine("def " + predicate + "():")
                    .appendLine("return true", 1)
                    .appendLine("\n");
        }

        for (String function : functions) {
            definitions.appendLine("def " + function + "():")
                    .appendLine("pass", 1)
                    .appendLine("\n");
        }

        if (tasksToParallelise > 0)
            program.appendLine("from threading import Thread\n\n");

        return program.appendBlock(definitions)
                .appendLine("if __name__ == '__main__':")
                .appendBlock(main, 1);
    }

    @Override
    protected String visitAtom(String name) {
        functions.add(name);
        return name + "()";
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
        return new Program().appendLine("if " + predicate + ":")
                .appendBlock(thenBranch, 1)
                .appendLine("else:")
                .appendBlock(elseBranch, 1);
    }

    @Override
    protected Program visitConcur(Program preStatement, Program firstThread, Program secondThread) {
        int threads = tasksToParallelise;
        int first = tasksToParallelise++;
        int second = tasksToParallelise++;
        return new Program().appendLine("def task" + first + "():")
                .appendBlock(firstThread, 1)
                .appendLine("\n")
                .appendLine("def task" + second + "():")
                .appendBlock(secondThread, 1)
                .appendLine("\n")
                .appendBlock(preStatement)
                .appendLine("threads" + threads + " = [Thread(target=task" + first + "), Thread(target=task" + second + ")]")
                .appendLine("for t in threads" + threads + ":")
                .appendLine("t.start()", 1)
                .appendLine("for t in threads" + threads + ":")
                .appendLine("t.join()", 1);
    }

    @Override
    protected Program visitCond(String predicate, Program thenBranch, Program elseBranch, Program postStatement) {
        return new Program().appendLine("if " + predicate + ":")
                .appendBlock(thenBranch, 1)
                .appendLine("else:")
                .appendBlock(elseBranch, 1)
                .appendBlock(postStatement);
    }

    @Override
    protected Program visitPara(Program preStatement, Program firstThread, Program secondThread, Program postStatement) {
        return visitConcur(preStatement, firstThread, secondThread)
                .appendBlock(postStatement);
    }

    @Override
    protected Program visitLoop(Program preStatement, String predicate, Program loopBody, Program postStatement) {
        return new Program().appendBlock(preStatement)
                .appendLine("while " + predicate + ":")
                .appendBlock(loopBody, 1)
                .appendBlock(postStatement);
    }

    @Override
    protected Program visitSeqSeq(Program first, Program then, Program last) {
        return new Program().appendBlock(first).appendBlock(then).appendBlock(last);
    }

    @Override
    protected Program visitRepeat(Program preStatement, Program loopBody, String predicate, Program postStatement) {
        return new Program().appendBlock(preStatement)
                .appendBlock(loopBody)
                .appendLine("while not " + predicate + ":")
                .appendBlock(loopBody, 1)
                .appendBlock(postStatement);
    }
}
