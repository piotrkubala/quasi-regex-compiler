package pl.edu.agh.kis.model;

import java.util.HashMap;
import java.util.Map;

public class DebugInfo {
    public int startLine;
    public int startCharacter;

    private final Map<String, Object> additionalInfo;

    public DebugInfo(int startLine, int startCharacter) {
        this.startLine = startLine;
        this.startCharacter = startCharacter;
        this.additionalInfo = new HashMap<>();
    }

    public void put(String name, Object value) {
        additionalInfo.put(name, value);
    }

    public Object get(String name) {
        return additionalInfo.get(name);
    }

    public Object remove(String name) {
        return additionalInfo.remove(name);
    }
}
