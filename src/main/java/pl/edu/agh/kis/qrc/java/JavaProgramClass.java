package pl.edu.agh.kis.qrc.java;

import java.util.HashMap;

public class JavaProgramClass {
    /**
     * maps integer (which is function number) to other functions that are used in code of this function
     */
    HashMap<Integer, JavaProgramCode> allClassMethods = new HashMap<>();
    HashMap<String, Integer> atomNameToFuncNumber = new HashMap<>();

    /**
     * every created function gets indexed by next natural number
     */
    private int createdFunctionsCounter = 0;

    public int getCreatedFunctionsCounterAndUpdate() {
        return createdFunctionsCounter++;
    }

    public void addFunctionToMaps(JavaProgramCode code) {
        if (!atomNameToFuncNumber.containsKey(code.functionName)) {
            atomNameToFuncNumber.put(code.functionName, code.functionNumber);
        }
        if (!allClassMethods.containsKey(code.functionNumber)) {
            allClassMethods.put(code.functionNumber, code);
        }
    }

    public void addNewMethodByName(String newFunctionName) {
        if (!atomNameToFuncNumber.containsKey(newFunctionName)) {
            atomNameToFuncNumber.put(newFunctionName, createdFunctionsCounter);
            allClassMethods.put(createdFunctionsCounter, new JavaProgramCode(this, newFunctionName, createdFunctionsCounter));

            createdFunctionsCounter++;
        }
    }

    public void removeFunctionByName(String functionName) {
        if (atomNameToFuncNumber.containsKey(functionName)) {
            int functionNumber = atomNameToFuncNumber.get(functionName);

            if (allClassMethods.containsKey(functionNumber)) {
                allClassMethods.remove(functionNumber);
            }
            atomNameToFuncNumber.remove(functionName);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("public class PatternClass {\n");

        for (JavaProgramCode code: allClassMethods.values()) {
            if (!code.containsAtomicFunction) {
                sb.append(code.getFunctionCodeAsStringBuilder(1));
            }
        }

        sb.append("}\n");

        return sb.toString();
    }
}
