package pl.edu.agh.kis;

import java.util.*;

public class PythonPatternGenerator extends PatternGenerator {

    private final Program program;
    private int tasksToParallelise = 0;
    private final Program passStatement = new Program().appendLine("pass");

    public PythonPatternGenerator() {
        this.program = new Program();
    }

    @Override
    public Program generate(PatternParser.PatternContext ctx) {
        Program main = visitPattern(ctx);
        Program definitions = new Program();

        for (Method method : methods) {
            definitions.appendLine("def " + method.name + "(" + String.join(", ", getFormalParameters(method.parameterTypes)) + "):");
            switch (method.returnType.name) {
                case "INTEGER" -> definitions.appendLine("return 0", 1);
                case "FLOATING" -> definitions.appendLine("return 0.0", 1);
                case "STRING" -> definitions.appendLine("return \"\"", 1);
                case "BOOLEAN" -> definitions.appendLine("return true", 1);
                case "VOID" -> definitions.appendLine("pass", 1);
                default -> definitions.appendLine("return None", 1);
            }
            definitions.appendLine("\n");
        }

        if (tasksToParallelise > 0)
            program.appendLine("from threading import Thread\n\n");

        return program.appendBlock(definitions)
                .appendLine("if __name__ == '__main__':")
                .appendBlock(main, 1);
    }

    @Override
    protected String mapType(Method.Type type) {
        return switch (type.name) {
            case "INTEGER" -> "int";
            case "FLOATING" -> "float";
            case "STRING" -> "str";
            case "BOOLEAN" -> "bool";
            case "OBJECT" -> "object";
            case "VOID" -> "void";
            default -> type.name;
        };
    }

    @Override
    protected String visitAtomicExpression(String name, List<String> parameters) {
        return String.format("%s(%s)", name, String.join(", ", parameters));
    }

    @Override
    protected Program visitAtomicStatement(String name, List<String> parameters) {
        return new Program().appendLine(visitAtomicExpression(name, parameters));
    }

    @Override
    protected Program visitSeq(Program first, Program then) {
        return new Program().appendBlock(first).appendBlock(then);
    }

    @Override
    protected Program visitBranch(String predicate, Program thenBranch, Program elseBranch) {
        return new Program().appendLine("if " + predicate + ":")
                .appendIf(thenBranch.isEmpty(), passStatement, thenBranch, 1)
                .appendLine("else:")
                .appendIf(elseBranch.isEmpty(), passStatement, elseBranch, 1);
    }

    @Override
    protected Program visitConcur(Program preStatement, Program firstThread, Program secondThread) {
        int threads = tasksToParallelise;
        int first = tasksToParallelise++;
        int second = tasksToParallelise++;
        return new Program().appendLine("def task" + first + "():")
                .appendIf(firstThread.isEmpty(), passStatement, firstThread, 1)
                .appendLine("\n")
                .appendLine("def task" + second + "():")
                .appendIf(secondThread.isEmpty(), passStatement, secondThread, 1)
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
                .appendIf(thenBranch.isEmpty(), passStatement, thenBranch, 1)
                .appendLine("else:")
                .appendIf(elseBranch.isEmpty(), passStatement, elseBranch, 1)
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
                .appendIf(loopBody.isEmpty(), passStatement, loopBody, 1)
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
                .appendIf(loopBody.isEmpty(), passStatement, loopBody, 1)
                .appendBlock(postStatement);
    }
}
