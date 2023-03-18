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
     * this 2 variables store references to the beginning and the end of the list of lines of the content of this function (only body)
     */
    JavaCodeLine javaLinesFirst;
    JavaCodeLine javaLinesLast;
    String indentSymbol;

    String functionName;
    int functionNumber;

    boolean isBooleanFunction = false;

    JavaProgramClass parentClass;


    public JavaProgramCode(JavaProgramClass parentClass_) {
        indentSymbol = "    ";

        prepare(parentClass_);
    }

    public JavaProgramCode(JavaProgramClass parentClass_, String indentString_) {
        indentSymbol = indentString_;

        prepare(parentClass_);
    }

    private void prepare(JavaProgramClass parentClass_) {
        parentClass = parentClass_;

        int functionCounter = parentClass.getCreatedFunctionsCounter();

        functionName = "fun" + functionCounter + "()";
        functionNumber = functionCounter;

        parentClass.addNewMethodByName(functionName);
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
        parentClass.addNewMethodByName(functionToCallName);

        appendToLastLineOfCode(functionToCallName);
    }

    public void addCodeAsFunction(JavaProgramCode code) {
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

        parentClass.removeFunctionByName(codeToAppend.functionName);
    }

    public StringBuilder getFunctionCodeAsStringBuilder(int numberOfIndents) {
        StringBuilder output = new StringBuilder();

        JavaCodeLine currentLine = javaLinesFirst;

        output.append(indentSymbol.repeat(numberOfIndents));

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

        output.append(indentSymbol.repeat(numberOfIndents));
        output.append("}\n\n");

        return output;
    }

    @Override
    public String toString() {
        return getFunctionCodeAsStringBuilder(1).toString();
    }
}
