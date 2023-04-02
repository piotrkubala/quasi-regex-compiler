package pl.edu.agh.kis;

import java.util.*;

public class PythonPatternGenerator extends PatternGenerator {

    private final Program program;
    private int tasksToParallelise = 0;

    public PythonPatternGenerator() {
        this.program = new Program();
    }

    @Override
    public Program generate(PatternParser.PatternContext ctx) {
        Program main = visitPattern(ctx);
        Program definitions = new Program();

        for (Method method : methods) {
            definitions.appendLine("def " + method.name + "(" + mangleNames(method.parameterTypes) + "):");
            switch (method.returnType.name) {
                case "INTEGER" -> definitions.appendLine("return 0", 1);
                case "FLOATING" -> definitions.appendLine("return 0.0", 1);
                case "STRING" -> definitions.appendLine("return \"\"", 1);
                case "BOOLEAN" -> definitions.appendLine("return true", 1);
                case "VOID" -> definitions.appendLine("pass", 1);
                default -> definitions.appendLine("return null", 1);
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
    protected String mangleNames(List<Method.Type> parameters) {
        Map<String, Integer> namesCount = new HashMap<>();
        List<String> names = new ArrayList<>();

        for (var param : parameters) {
            String name = mapType(param).charAt(0) + mapType(param).chars().skip(1)
                    .filter(Character::isUpperCase)
                    .map(Character::toLowerCase)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            namesCount.put(name, namesCount.getOrDefault(name, -1) + 1);

            names.add(name + namesCount.get(name));
        }

        return String.join(", ", names);
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
