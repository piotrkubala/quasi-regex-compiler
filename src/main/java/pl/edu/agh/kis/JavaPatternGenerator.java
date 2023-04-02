package pl.edu.agh.kis;

import java.util.*;

public class JavaPatternGenerator extends PatternGenerator {

    private final Program program;
    private int tasksToParallelise = 0;

    public JavaPatternGenerator() {
        this.program = new Program();
    }

    @Override
    public Program generate(PatternParser.PatternContext ctx) {
        Program main = visitPattern(ctx);
        Program definitions = new Program();

        for (Method method : methods) {
            String returnType = mapType(method.returnType);

            definitions.appendLine("private static " + returnType + " " + method.name + "(" + mangleNames(method.parameterTypes) + ") {");
            switch (method.returnType.name) {
                case "INTEGER" -> definitions.appendLine("return 0;", 1);
                case "FLOATING" -> definitions.appendLine("return 0.0;", 1);
                case "STRING" -> definitions.appendLine("return \"\";", 1);
                case "BOOLEAN" -> definitions.appendLine("return true;", 1);
                case "VOID" -> definitions.appendLine("", 1);
                default -> definitions.appendLine("return null;", 1);
            }
            definitions.appendLine("}\n");
        }

        return program.appendLine("public class Program {")
                .appendBlock(definitions, 1)
                .appendLine("public static void main(String[] args) {", 1)
                .appendBlock(main, 2)
                .appendLine("}", 1)
                .appendLine("}");
    }

    @Override
    protected String mapType(Method.Type type) {
        return switch (type.name) {
            case "INTEGER" -> "int";
            case "FLOATING" -> "float";
            case "STRING" -> "String";
            case "BOOLEAN" -> "bool";
            case "OBJECT" -> "Object";
            case "VOID" -> "void";
            default -> type.name;
        };
    }

    @Override
    protected String mangleNames(List<Method.Type> parameters) {
        Map<String, Integer> namesCount = new HashMap<>();
        List<String> names = new ArrayList<>();

        for (var param : parameters) {
            String name = mapType(param).toLowerCase().charAt(0) + mapType(param).chars().skip(1)
                    .filter(Character::isUpperCase)
                    .map(Character::toLowerCase)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            namesCount.put(name, namesCount.getOrDefault(name, -1) + 1);

            names.add(mapType(param) + " " + name + namesCount.get(name));
        }

        return String.join(", ", names);
    }

    @Override
    protected String visitAtomicExpression(String name, List<String> parameters) {
        return String.format("%s(%s)", name, String.join(", ", parameters));
    }

    @Override
    protected Program visitAtomicStatement(String name, List<String> parameters) {
        return new Program().appendLine(visitAtomicExpression(name, parameters) + ";");
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
        int first = tasksToParallelise++;
        int second = tasksToParallelise++;
        return new Program().appendBlock(preStatement)
                .appendLine("Thread thread" + first + " = new Thread(() -> {")
                .appendBlock(firstThread, 1)
                .appendLine("});")
                .appendLine("Thread thread" + second + " = new Thread(() -> {")
                .appendBlock(secondThread, 1)
                .appendLine("});")
                .appendLine("thread" + first + ".start()")
                .appendLine("thread" + second + ".start()")
                .appendLine("try {")
                .appendLine("thread" + first + ".join()", 1)
                .appendLine("thread" + second + ".join()", 1)
                .appendLine("} catch (InterruptedException e) {")
                .appendLine("", 1)
                .appendLine("}");
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
        return visitConcur(preStatement, firstThread, secondThread)
                .appendBlock(postStatement);
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
