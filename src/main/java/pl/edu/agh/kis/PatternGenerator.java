package pl.edu.agh.kis;

import java.util.ArrayList;
import java.util.List;

public abstract class PatternGenerator extends PatternBaseVisitor<Program> implements PatternVisitor<Program> {
    @Override
    public Program visitPattern(PatternParser.PatternContext ctx) {
        if (ctx.STRING() != null) {
            return new Program().appendLine(visitString(ctx.STRING().getText()));
        }
        else if (ctx.ATOM() != null) {
            return new Program().appendLine(visitAtom(ctx.ATOM().getText()));
        } else {
            String patternName = ctx.pattern_name().PATTERN_NAME_LEX().getText();
            List<PatternParser.PatternContext> arguments = getArguments(ctx);

            switch (patternName) {
                case "Seq" -> {
                    Program first = visitPattern(arguments.get(0));
                    Program then = visitPattern(arguments.get(1));

                    return visitSeq(first, then);
                }
                case "Branch" -> {
                    checkArgumentTypes(arguments, 0, patternName);

                    String predicate;

                    if (arguments.get(0).STRING() != null)
                        predicate = visitString(arguments.get(0).STRING().getText());
                    else
                        predicate = visitPredicate(arguments.get(0).ATOM().getText());

                    Program thenBranch = visitPattern(arguments.get(1));
                    Program elseBranch = visitPattern(arguments.get(2));

                    return visitBranch(predicate, thenBranch, elseBranch);
                }
                case "Concur" -> {
                    Program preStatement = visitPattern(arguments.get(0));
                    Program firstThread = visitPattern(arguments.get(1));
                    Program secondThread = visitPattern(arguments.get(2));

                    return visitConcur(preStatement, firstThread, secondThread);
                }
                case "Cond", "If" -> {
                    checkArgumentTypes(arguments, 0, patternName);

                    String predicate;

                    if (arguments.get(0).STRING() != null)
                        predicate = visitString(arguments.get(0).STRING().getText());
                    else
                        predicate = visitPredicate(arguments.get(0).ATOM().getText());

                    Program thenBranch = visitPattern(arguments.get(1));
                    Program elseBranch = visitPattern(arguments.get(2));
                    Program postStatement = visitPattern(arguments.get(3));

                    return visitCond(predicate, thenBranch, elseBranch, postStatement);
                }
                case "Para" -> {
                    Program preStatement = visitPattern(arguments.get(0));
                    Program firstThread = visitPattern(arguments.get(1));
                    Program secondThread = visitPattern(arguments.get(2));
                    Program postStatement = visitPattern(arguments.get(3));

                    return visitPara(preStatement, firstThread, secondThread, postStatement);
                }
                case "Loop" -> {
                    checkArgumentTypes(arguments, 1, patternName);

                    Program preStatement = visitPattern(arguments.get(0));

                    String predicate;

                    if (arguments.get(1).STRING() != null)
                        predicate = visitString(arguments.get(1).STRING().getText());
                    else
                        predicate = visitPredicate(arguments.get(1).ATOM().getText());

                    Program loopBody = visitPattern(arguments.get(2));
                    Program postStatement = visitPattern(arguments.get(3));

                    return visitLoop(preStatement, predicate, loopBody, postStatement);
                }
                case "SeqSeq" -> {
                    Program first = visitPattern(arguments.get(0));
                    Program then = visitPattern(arguments.get(1));
                    Program last = visitPattern(arguments.get(2));

                    return visitSeqSeq(first, then, last);
                }
                case "Repeat" -> {
                    checkArgumentTypes(arguments, 2, patternName);

                    Program preStatement = visitPattern(arguments.get(0));
                    Program loopBody = visitPattern(arguments.get(1));

                    String predicate;

                    if (arguments.get(2).STRING() != null)
                        predicate = visitString(arguments.get(2).STRING().getText());
                    else
                        predicate = visitPredicate(arguments.get(2).ATOM().getText());

                    Program postStatement = visitPattern(arguments.get(3));

                    return visitRepeat(preStatement, loopBody, predicate, postStatement);
                }
                default -> {
                    return new Program();
                }
            }
        }
    }

    public abstract Program generate(PatternParser.PatternContext ctx);

    protected abstract String visitAtom(String name);

    protected abstract String visitPredicate(String name);

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

    private List<PatternParser.PatternContext> getArguments(PatternParser.PatternContext ctx) {
        List<PatternParser.PatternContext> args = new ArrayList<>();

        PatternParser.ArgumentsContext curr = ctx.arguments;

        while (curr.args_with_delim != null) {
            args.add(curr.pattern());
            curr = curr.args_with_delim.arguments;
        }
        args.add(curr.pattern());

        return args;
    }

    private void checkArgumentTypes(List<PatternParser.PatternContext> args, int argPos, String patternName) {
        if (args.get(argPos).ATOM() == null && args.get(argPos).STRING() == null) {
            throw GeneratorException.argumentTypeMismatch(args.get(argPos).getStart().getLine(),
                    args.get(argPos).getStart().getCharPositionInLine(),
                    argPos, patternName);
        }
    }
}
