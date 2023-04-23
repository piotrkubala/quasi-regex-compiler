package pl.edu.agh.kis.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    public Node parent;
    public List<Node> children;
    public DebugInfo debugInfo;

    public Node(DebugInfo debugInfo) {
        this.children = new ArrayList<>();
        this.debugInfo = debugInfo;
    }

    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }
}
