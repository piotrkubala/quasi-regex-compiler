package pl.edu.agh.kis.model;

public class ValueNode extends Node {
    public Type type;
    public String value;

    public ValueNode(Type type, String value, DebugInfo debugInfo) {
        super(debugInfo);
        this.type = type;
        this.value = value;
    }
}
