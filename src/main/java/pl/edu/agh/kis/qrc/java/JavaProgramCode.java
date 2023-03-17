package pl.edu.agh.kis.qrc.java;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class JavaProgramCode {
    static class JavaCodeLine {
        StringBuilder line = new StringBuilder();
        int numberOfIndentsToGoUp = 0;

        JavaCodeLine nextCodeLine;

        public JavaCodeLine(StringBuilder lineOfCode_, int numberOfIndentsToGoUp_) {
            line = lineOfCode_;
            numberOfIndentsToGoUp = numberOfIndentsToGoUp_;
        }
    }

    private static int createdFunctionsCounter = 0;

    /**
     * this 2 variables store references to the beginning and the end of the list of lines of the content of this function (only body)
     */
    JavaCodeLine javaLinesFirst;
    JavaCodeLine javaLinesLast;
    String indentSymbol;

    String functionName;
    int functionNumber;

    /**
     * maps integer (which is function number) to other functions that are used in code of this function
     * used only to indicate 'real functions' - functions which are going to have code template inside
     */
    HashMap<Integer, JavaProgramCode> connectedFunctions = new HashMap<>();

    public JavaProgramCode() {
        indentSymbol = "    ";

        prepare();
    }

    public JavaProgramCode(String indentString) {
        indentSymbol = indentString;

        prepare();
    }

    private void prepare() {
        functionName = "fun" + createdFunctionsCounter;
        functionNumber = createdFunctionsCounter;
        createdFunctionsCounter++;
    }

    public void AppendLineOfCode(String lineToAppend, int indentChange) {
        JavaCodeLine newLineOfCode = new JavaCodeLine(new StringBuilder(lineToAppend), indentChange);

        if (javaLinesLast == null) {
            javaLinesFirst = javaLinesLast = newLineOfCode;
        } else {
            javaLinesLast.nextCodeLine = newLineOfCode;
            javaLinesLast = newLineOfCode;
        }
    }

    public void AppendCode(JavaProgramCode codeToAppend, boolean indentFirstLine) {
        int indentChange = indentFirstLine ? 1 : 0;

        if (codeToAppend.javaLinesLast == null) {
            return;
        }

        codeToAppend.javaLinesFirst.numberOfIndentsToGoUp += indentChange;
        codeToAppend.javaLinesLast.numberOfIndentsToGoUp -= indentChange;

        if (javaLinesLast == null) {
            javaLinesFirst = codeToAppend.javaLinesFirst;
        } else {
            javaLinesLast.nextCodeLine = codeToAppend.javaLinesFirst;
        }

        javaLinesLast = codeToAppend.javaLinesLast;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        int numberOfIndents = 0;

        JavaCodeLine currentLine = javaLinesFirst;

        while (currentLine != null) {
            numberOfIndents += currentLine.numberOfIndentsToGoUp;
            output.append(indentSymbol.repeat(numberOfIndents));
            output.append(currentLine.line);
            output.append('\n');

            currentLine = currentLine.nextCodeLine;
        }

        return output.toString();
    }
}
