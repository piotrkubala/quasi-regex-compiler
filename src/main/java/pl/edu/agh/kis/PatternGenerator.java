package pl.edu.agh.kis;

public abstract class PatternGenerator extends PatternBaseVisitor<Program> implements PatternVisitor<Program> {
    @Override
    public Program visitPattern(PatternParser.PatternContext ctx) {
        if (ctx.ATOM() != null) {
            return (new Program()).appendLine(visitAtom(ctx.ATOM().getText()));
        } else {
            String patternName = ctx.pattern_name().PATTERN_NAME_LEX().getText();
            switch (patternName) {
                case "Seq" -> {
                    Program first = visitPattern(ctx.arguments.pattern());
                    Program then = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());

                    return visitSeq(first, then);
                }
                case "Branch" -> {
                    String predicate = visitPredicate(ctx.arguments.pattern().ATOM().getText());
                    Program thenBranch = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    Program elseBranch = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitBranch(predicate, thenBranch, elseBranch);
                }
                case "Concur" -> {
                    Program preStatement = visitPattern(ctx.arguments.pattern());
                    Program firstThread = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    Program secondThread = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitConcur(preStatement, firstThread, secondThread);
                }
                case "Cond", "If" -> {
                    String predicate = visitPredicate(ctx.arguments.pattern().ATOM().getText());
                    Program thenBranch = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    Program elseBranch = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());
                    Program postStatement = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitCond(predicate, thenBranch, elseBranch, postStatement);
                }
                case "Para" -> {
                    Program preStatement = visitPattern(ctx.arguments.pattern());
                    Program firstThread = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    Program secondThread = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());
                    Program postStatement = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitPara(preStatement, firstThread, secondThread, postStatement);
                }
                case "Loop" -> {
                    Program preStatement = visitPattern(ctx.arguments.pattern());
                    String predicate = visitPredicate(ctx.arguments
                            .args_with_delim.arguments.pattern().ATOM().getText());
                    Program loopBody = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());
                    Program postStatement = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitLoop(preStatement, predicate, loopBody, postStatement);
                }
                case "SeqSeq" -> {
                    Program first = visitPattern(ctx.arguments.pattern());
                    Program then = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    Program last = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

                    return visitSeqSeq(first, then, last);
                }
                case "Repeat" -> {
                    Program preStatement = visitPattern(ctx.arguments.pattern());
                    Program loopBody = visitPattern(ctx.arguments
                            .args_with_delim.arguments.pattern());
                    String predicate = visitPredicate(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern().ATOM().getText());
                    Program postStatement = visitPattern(ctx.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments
                            .args_with_delim.arguments.pattern());

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
}
