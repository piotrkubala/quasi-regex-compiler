package pl.edu.agh.kis.validation;

import pl.edu.agh.kis.model.*;
import static pl.edu.agh.kis.validation.ValidatorException.ExceptionType.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class DefaultValidator implements Validator {

    private static final Map<String, Integer> argCounts = Map.ofEntries(
            entry("Seq", 2), entry("If", 2),
            entry("Alt", 3), entry("Branch", 3), entry("BranchRe", 3),
            entry("Concur", 3), entry("ConcurRe", 3), entry("SeqSeq", 3),
            entry("Cond", 4), entry("Para", 4), entry("Loop", 4), entry("Repeat", 4),
            entry("Iter", 5)
    );

    private static final Map<String, Integer> predicatePositions = Map.ofEntries(
            entry("Branch", 0), entry("Cond", 0), entry("If", 0), entry("Alt", 0),
            entry("Loop", 1), entry("Repeat", 2), entry("Iter", 2)
    );

    private static final Map<String, List<String>> specialPatterns = Map.ofEntries(
            entry("BranchRe", List.of("Branch", "Cond")),
            entry("ConcurRe", List.of("Concur", "Para"))
    );

    @Override
    public void validate(Model model) {
        visitNode(model.mainBody);
    }

    public void visitNode(Node node) {
        if (node instanceof WorkflowNode) {
            String name = ((WorkflowNode) node).name;

            if (specialPatterns.containsKey(name))
            {
                String corresponding = specialPatterns.get(name).get(0);
                node.debugInfo.put("corresponding", corresponding);

                WorkflowNode parent = (WorkflowNode) node.getParent();
                if (parent == null || !Objects.equals(parent.name, "Seq")) // parent is not Seq
                    throw ValidatorException.of(RETURN_PATTERN_MISMATCH, node.debugInfo);

                WorkflowNode sibling = (WorkflowNode) node.getParent().getChildren().get(0);

                if (sibling == null || !Objects.equals(sibling.name, corresponding)) // sibling is not the corresponding pattern
                    throw ValidatorException.of(RETURN_PATTERN_MISMATCH, node.debugInfo);

                parent.name = specialPatterns.get(name).get(1);

                WorkflowNode s1 = new WorkflowNode("Seq", new DebugInfo(-1, -1));
                s1.addChild(sibling.getChildren().get(1));
                s1.addChild(node.getChildren().get(0));

                WorkflowNode s2 = new WorkflowNode("Seq", new DebugInfo(-1, -1));
                s2.addChild(sibling.getChildren().get(2));
                s2.addChild(node.getChildren().get(1));

                parent.children = List.of(sibling.getChildren().get(0), s1, s2, node.getChildren().get(2));
            }

            Integer count = argCounts.get(name);
            node.debugInfo.put("expected", count);
            node.debugInfo.put("got", node.getChildren().size());

            if (count != null && count != node.getChildren().size()) {
                throw ValidatorException.of(ARGUMENT_COUNT_MISMATCH, node.debugInfo);
            }

            Integer predicatePosition = predicatePositions.get(name);

            if (predicatePosition != null) {
                node.debugInfo.put("predicatePosition", predicatePosition);
                Node child = node.getChildren().get(predicatePosition);

                if (child instanceof WorkflowNode || child instanceof EmptyNode) {
                    throw ValidatorException.of(NON_BOOLEAN_PREDICATE, node.debugInfo);
                }

                if (child instanceof MethodNode) {
                    if (((MethodNode) child).method.returnType == Type.Void)
                        ((MethodNode) child).method.returnType = Type.Boolean;

                    ((MethodNode) child).statement = false;
                }
            }
        }

        for (Node child : node.getChildren()) {
            visitNode(child);
        }
    }
}
