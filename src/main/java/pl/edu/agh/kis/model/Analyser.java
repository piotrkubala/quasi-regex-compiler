package pl.edu.agh.kis.model;

import pl.edu.agh.kis.PatternBaseVisitor;
import pl.edu.agh.kis.PatternParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates Model by analysing given pattern.
 */
public class Analyser {

    public static Model analyse(PatternParser parser) {
        PatternVisitor visitor = new PatternVisitor();
        Node mainBody = visitor.visitPattern(parser.pattern());

        return new Model(mainBody, visitor.methods);
    }

    private static class PatternVisitor extends PatternBaseVisitor<Node> implements pl.edu.agh.kis.PatternVisitor<Node> {
        Set<Method> methods;

        protected PatternVisitor() {
            methods = new HashSet<>();
        }

        @Override
        public Node visitPattern(PatternParser.PatternContext ctx) {
            DebugInfo debugInfo = new DebugInfo(ctx.getStart().getLine(),
                                                ctx.getStart().getCharPositionInLine());

            if (ctx.EMPTY() != null) {
                return new EmptyNode(debugInfo);
            } else if (ctx.ATOM() != null) {
                return new StringNode(ctx.ATOM().getText(), debugInfo);
            } else if (ctx.STRING() != null) {
                return new StringNode(ctx.STRING().getText()
                        .translateEscapes()
                        .replaceAll("^.|.$", ""),
                        debugInfo);
            } else if (ctx.method() != null) {
                return visitMethod(ctx.method());
            } else {
                String name = ctx.patternName().PATTERN_NAME_LEX().getText();
                debugInfo.put("name", name);
                WorkflowNode node = new WorkflowNode(name, debugInfo);

                for (var arg : ctx.args) {
                    node.addChild(visitPattern(arg));
                }

                return node;
            }
        }

        @Override
        public Node visitMethod(PatternParser.MethodContext ctx) {
            DebugInfo debugInfo = new DebugInfo(ctx.getStart().getLine(),
                                                ctx.getStart().getCharPositionInLine());
            String name = ctx.ATOM().getText();
            List<Type> parameterTypes = new ArrayList<>();
            Type returnType;

            if (ctx.returnType != null) {
                returnType = Type.of(ctx.returnType.getText()
                        .translateEscapes()
                        .replaceAll("^.|.$", ""));
            } else {
                returnType = Type.Void;
            }

            Method method = new Method(name, parameterTypes, returnType);
            MethodNode node = new MethodNode(method, debugInfo);

            for (var arg : ctx.args) {
                DebugInfo argDebugInfo = new DebugInfo(arg.getStart().getLine(),
                                                       arg.getStart().getCharPositionInLine());

                if (arg.method() != null) {
                    MethodNode child = (MethodNode) visitMethod(arg.method());
                    if (child.method.returnType == Type.Void) {
                        child.method.returnType = Type.Object;
                    }
                    child.statement = false;
                    parameterTypes.add(child.method.returnType);
                    node.addChild(child);
                } else {
                    Type type;
                    String value;

                    if (arg.typedExpr() != null) {
                        type = Type.of(arg.typedExpr().type.getText()
                                .translateEscapes()
                                .replaceAll("^.|.$", ""));
                        value = arg.typedExpr().expr.getText()
                                .translateEscapes()
                                .replaceAll("^.|.$", "");
                    } else {
                        value = arg.getText();

                        if (arg.INTEGER() != null) {
                            type = Type.Integer;
                        } else if (arg.FLOATING() != null) {
                            type = Type.Floating;
                        } else if (arg.STRING() != null) {
                            type = Type.String;
                        } else if (arg.BOOLEAN() != null) {
                            type = Type.Boolean;
                        } else {
                            type = Type.Object;
                        }
                    }

                    parameterTypes.add(type);
                    node.addChild(new ValueNode(type, value, argDebugInfo));
                }
            }

            methods.add(method);

            return node;
        }
    }
}
