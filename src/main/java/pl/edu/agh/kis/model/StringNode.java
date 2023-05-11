package pl.edu.agh.kis.model;

public class StringNode extends Node{
    public String string;

    public StringNode(String string, DebugInfo debugInfo) {
        super(debugInfo);
        this.string = string;
    }
}
