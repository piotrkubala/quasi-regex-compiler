package pl.edu.agh.kis;

import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static pl.edu.agh.kis.Method.Type;

public abstract class PatternGenerator extends PatternBaseVisitor<Program> implements PatternVisitor<Program> {
    Set<Method> methods = new HashSet<>();

    @Override
    public Program visitPattern(PatternParser.PatternContext ctx) {
        if (ctx.STRING() != null) {
            return new Program().appendLine(visitString(ctx.STRING().getText()));
        } else if (ctx.method() != null) {
            introduceMethod(ctx.method(), Type.Void);

            return visitAtomicStatement(
                    ctx.method().ATOM().getText(),
                    ctx.method().args.stream().map(RuleContext::getText).collect(Collectors.toList()));
        } else {
            String patternName = ctx.patternName().PATTERN_NAME_LEX().getText();
            switch (patternName) {
                case "Seq" -> {
                    Program first = visitPattern(ctx.args.get(0));
                    Program then = visitPattern(ctx.args.get(1));

                    return visitSeq(first, then);
                }
                case "Branch" -> {
                    String predicate = getPredicate(ctx.args.get(0), 0, patternName);
                    Program thenBranch = visitPattern(ctx.args.get(1));
                    Program elseBranch = visitPattern(ctx.args.get(2));

                    return visitBranch(predicate, thenBranch, elseBranch);
                }
                case "Concur" -> {
                    Program preStatement = visitPattern(ctx.args.get(0));
                    Program firstThread = visitPattern(ctx.args.get(1));
                    Program secondThread = visitPattern(ctx.args.get(2));

                    return visitConcur(preStatement, firstThread, secondThread);
                }
                case "Cond", "If" -> {
                    String predicate = getPredicate(ctx.args.get(0), 0, patternName);
                    Program thenBranch = visitPattern(ctx.args.get(1));
                    Program elseBranch = visitPattern(ctx.args.get(2));
                    Program postStatement = visitPattern(ctx.args.get(3));

                    return visitCond(predicate, thenBranch, elseBranch, postStatement);
                }
                case "Para" -> {
                    Program preStatement = visitPattern(ctx.args.get(0));
                    Program firstThread = visitPattern(ctx.args.get(1));
                    Program secondThread = visitPattern(ctx.args.get(2));
                    Program postStatement = visitPattern(ctx.args.get(3));

                    return visitPara(preStatement, firstThread, secondThread, postStatement);
                }
                case "Loop" -> {
                    Program preStatement = visitPattern(ctx.args.get(0));
                    String predicate = getPredicate(ctx.args.get(1), 1, patternName);
                    Program loopBody = visitPattern(ctx.args.get(2));
                    Program postStatement = visitPattern(ctx.args.get(3));

                    return visitLoop(preStatement, predicate, loopBody, postStatement);
                }
                case "SeqSeq" -> {
                    Program first = visitPattern(ctx.args.get(0));
                    Program then = visitPattern(ctx.args.get(1));
                    Program last = visitPattern(ctx.args.get(2));

                    return visitSeqSeq(first, then, last);
                }
                case "Repeat" -> {
                    Program preStatement = visitPattern(ctx.args.get(0));
                    Program loopBody = visitPattern(ctx.args.get(1));
                    String predicate = getPredicate(ctx.args.get(2), 2, patternName);
                    Program postStatement = visitPattern(ctx.args.get(3));

                    return visitRepeat(preStatement, loopBody, predicate, postStatement);
                }
                default -> throw GeneratorException.unknownPattern(
                        ctx.getStart().getLine(),
                        ctx.getStart().getCharPositionInLine(),
                        patternName
                );
            }
        }
    }

    public abstract Program generate(PatternParser.PatternContext ctx);

    protected abstract String mangleNames(List<Type> parameters);

    protected abstract String mapType(Type type);

    protected abstract String visitAtomicExpression(String name, List<String> parameters);

    protected abstract Program visitAtomicStatement(String name, List<String> parameters);

    protected abstract Program visitSeq(Program first, Program then);

    protected abstract Program visitBranch(String predicate, Program thenBranch, Program elseBranch);

    protected abstract Program visitConcur(Program preStatement, Program firstThread, Program secondThread);

    protected abstract Program visitCond(String predicate, Program thenBranch, Program elseBranch, Program postStatement);

    protected abstract Program visitPara(Program preStatement, Program firstThread, Program secondThread, Program postStatement);

    protected abstract Program visitLoop(Program preStatement, String predicate, Program loopBody, Program postStatement);

    protected abstract Program visitSeqSeq(Program first, Program then, Program last);

    protected abstract Program visitRepeat(Program preStatement, Program loopBody, String predicate, Program postStatement);

    private String visitString(String string) {
        return string.translateEscapes().replaceAll("^.|.$", "");
    }

    private void introduceMethod(PatternParser.MethodContext ctx, Type returnType) {
        String name = ctx.ATOM().getText();
        List<Type> parameterTypes = new ArrayList<>();

        for (var param : ctx.args) {
            if (param.INTEGER() != null)
                parameterTypes.add(Type.Integer);
            else if (param.FLOATING() != null)
                parameterTypes.add(Type.Floating);
            else if (param.BOOLEAN() != null)
                parameterTypes.add(Type.Boolean);
            else if (param.STRING() != null)
                parameterTypes.add(Type.String);
            else {
                introduceMethod(param.method(), Type.Object);
                parameterTypes.add(Type.Object);
            }
        }

        methods.add(new Method(name, parameterTypes, returnType));
    }

    private String getPredicate(PatternParser.PatternContext arg, int argPos, String patternName) {
        if (arg.method() == null && arg.STRING() == null) {
            throw GeneratorException.argumentTypeMismatch(arg.getStart().getLine(),
                    arg.getStart().getCharPositionInLine(),
                    argPos, patternName);
        }

        if (arg.STRING() != null)
            return visitString(arg.STRING().getText());
        else {
            introduceMethod(arg.method(), Type.Boolean);

            return visitAtomicExpression(
                    arg.method().ATOM().getText(),
                    arg.method().args.stream().map(RuleContext::getText).collect(Collectors.toList())
            );
        }
    }
}
