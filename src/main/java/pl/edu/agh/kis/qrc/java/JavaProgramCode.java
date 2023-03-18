package pl.edu.agh.kis.qrc.java;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * it is an abstraction of a java code
 * it is a recurrent structure with functions that are being called stored in 'connectedFunctions' object
 */
public class JavaProgramCode {
    static class JavaCodeLine {
        StringBuilder line;
        int numberOfIndentsToGoUp;

        JavaCodeLine nextCodeLine;

        public JavaCodeLine(StringBuilder lineOfCode_, int numberOfIndentsToGoUp_) {
            line = lineOfCode_;
            numberOfIndentsToGoUp = numberOfIndentsToGoUp_;
        }
    }

    /**
     * every created function gets indexed by next natural number
     */
    private static int createdFunctionsCounter = 0;

    /**
     * maps integer (which is function number) to other functions that are used in code of this function
     */
    private static HashMap<Integer, JavaProgramCode> allFunctions = new HashMap<>();
    private static HashMap<String, Integer> atomNameToFuncNumber = new HashMap<>();

    /**
     * this 2 variables store references to the beginning and the end of the list of lines of the content of this function (only body)
     */
    JavaCodeLine javaLinesFirst;
    JavaCodeLine javaLinesLast;
    String indentSymbol;

    String functionName;
    int functionNumber;

    boolean isBooleanFunction = false;


    public JavaProgramCode() {
        indentSymbol = "    ";

        prepare();
    }

    public JavaProgramCode(String indentString) {
        indentSymbol = indentString;

        prepare();
    }

    private void prepare() {
        functionName = "fun" + createdFunctionsCounter + "()";
        functionNumber = createdFunctionsCounter;
        createdFunctionsCounter++;
    }

    /**
     * also appends the function call to the last line
     * @param code
     */
    public void addCodeAsBooleanFunction(JavaProgramCode code) {
        addCodeAsFunction(code);
        code.isBooleanFunction = true;
    }

    public void createNewEmptyFunctionIfNotExistsAndAppendCall(String functionToCallName) {
        if (!atomNameToFuncNumber.containsKey(functionToCallName)) {
            atomNameToFuncNumber.put(functionToCallName, createdFunctionsCounter);

            // createdFunctionCounter also gets updated here
            allFunctions.put(createdFunctionsCounter, new JavaProgramCode());

            appendToLastLineOfCode(functionToCallName);
        }
    }

    public void addCodeAsFunction(JavaProgramCode code) {
        allFunctions.put(code.functionNumber, code);
        appendToLastLineOfCode(code.functionName);
    }

    public void appendToLastLineOfCode(String textToAppend) {
        if (javaLinesLast == null) {
            appendLineOfCode(textToAppend, 0);
        } else {
            javaLinesLast.line.append(textToAppend);
        }
    }

    public void appendLineOfCode(String lineToAppend, int indentChange) {
        JavaCodeLine newLineOfCode = new JavaCodeLine(new StringBuilder(lineToAppend), indentChange);

        if (javaLinesLast == null) {
            javaLinesFirst = javaLinesLast = newLineOfCode;
        } else {
            javaLinesLast.nextCodeLine = newLineOfCode;
            javaLinesLast = newLineOfCode;
        }
    }

    public void appendCode(JavaProgramCode codeToAppend, int indentChange) {
        if (codeToAppend.javaLinesLast == null) {
            return;
        }

        codeToAppend.javaLinesFirst.numberOfIndentsToGoUp += indentChange;

        if (javaLinesLast == null) {
            javaLinesFirst = codeToAppend.javaLinesFirst;
        } else {
            javaLinesLast.nextCodeLine = codeToAppend.javaLinesFirst;
        }

        javaLinesLast = codeToAppend.javaLinesLast;

        allFunctions.remove(codeToAppend.functionNumber);
    }

    public StringBuilder getFunctionCodeAsStringBuilder() {
        StringBuilder output = new StringBuilder();
        int numberOfIndents = 1;

        JavaCodeLine currentLine = javaLinesFirst;

        if (isBooleanFunction) {
            output.append("public static boolean ");
        } else {
            output.append("public static void ");
        }
        output.append(functionName);
        output.append(" {\n");

        while (currentLine != null) {
            numberOfIndents += currentLine.numberOfIndentsToGoUp;

            output.append(indentSymbol.repeat(numberOfIndents));
            output.append(currentLine.line);
            output.append('\n');

            currentLine = currentLine.nextCodeLine;
        }

        output.append("}");

        return output;
    }

    @Override
    public String toString() {
        return getFunctionCodeAsStringBuilder().toString();
    }

    public static String printCodeAsClass() {
        StringBuilder sb = new StringBuilder();

        for (JavaProgramCode code: allFunctions.values()) {
            sb.append(code.getFunctionCodeAsStringBuilder());
        }

        return sb.toString();
    }
}
