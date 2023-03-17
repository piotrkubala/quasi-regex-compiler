package pl.edu.agh.kis.qrc.java;

import java.util.HashMap;
import java.util.HashSet;

public class JavaProgramCode {
    StringBuilder outputString = new StringBuilder();
    StringBuilder currentTabs = new StringBuilder();
    String tabulatorStr;

    HashSet<String> functionsNames = new HashSet<>();

    public JavaProgramCode() {
        tabulatorStr = "    ";

        prepareProgram();
    }

    public JavaProgramCode(String tabulator) {
        tabulatorStr = tabulator;

        prepareProgram();
    }

    public void prepareProgram() {
        outputString.append("public class Program ");
        openBlock();
        appendTabs();
        outputString.append("public void program() ");
        openBlock();
    }

    public void startLoop() {
        appendTabs();
        outputString.append("while ");
    }

    private void appendTabs() {
        outputString.append(tabulatorStr);
    }

    private void addToSet(String functionName) {
        functionsNames.add(functionName);
    }

    public void addBoolFunction(String functionName) {
        outputString.append(functionName);
        outputString.append("()");
        addToSet(functionName);
    }

    public void addVoidFunction(String functionName) {
        appendTabs();
        outputString.append(functionName);
        outputString.append("();\n");
        addToSet(functionName);
    }

    public void openBrackets() {
        outputString.append("(");
    }

    public void closeBracket() {
        outputString.append(")");
    }

    public void openBlock() {
        outputString.append("{\n");
        currentTabs.append(tabulatorStr);
    }

    public void closeBlock() {
        outputString.append("\n");
        appendTabs();
        outputString.append("}\n");
        currentTabs.setLength(currentTabs.length() - 1);
    }

    @Override
    public String toString() {
        return outputString.toString();
    }
}
