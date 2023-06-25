package pl.edu.agh.kis.validation;

import pl.edu.agh.kis.model.*;
import static pl.edu.agh.kis.validation.ValidatorException.ExceptionType.*;

import java.util.Map;
import static java.util.Map.entry;

public class DefaultValidator implements Validator {

    private static final Map<String, Integer> argCounts = Map.ofEntries(
            entry("Seq", 2), entry("If", 2),
            entry("Alt", 3), entry("Branch", 3), entry("Concur", 3), entry("SeqSeq", 3),
            entry("Cond", 4), entry("Para", 4), entry("Loop", 4), entry("Repeat", 4),
            entry("Iter", 5)
    );

    private static final Map<String, Integer> predicatePositions = Map.ofEntries(
            entry("Branch", 0), entry("Cond", 0), entry("If", 0), entry("Alt", 0),
            entry("Loop", 1), entry("Repeat", 2), entry("Iter", 2)
    );

    @Override
    public void validate(Model model) {
        visitNode(model.mainBody);
    }

    public void visitNode(Node node) {
        if (node instanceof WorkflowNode) {
            String name = ((WorkflowNode) node).name;

            Integer count = argCounts.get(name);
            node.debugInfo.put("expected", count);
            node.debugInfo.put("got", node.getChildren().size());

            if (count != null && count != node.getChildren().size()) {
                throw ValidatorException.of(ARGUMENT_COUNT_MISMATCH, node.debugInfo);
            }

            node.debugInfo.remove("expected");
            node.debugInfo.remove("got");

            Integer predicatePosition = predicatePositions.get(name);
            node.debugInfo.put("predicatePosition", predicatePosition);

            if (predicatePosition != null) {
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

            node.debugInfo.remove("predicatePosition");
        }

        for (Node child : node.getChildren()) {
            visitNode(child);
        }
    }
}
