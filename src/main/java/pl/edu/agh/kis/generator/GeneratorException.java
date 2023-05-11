package pl.edu.agh.kis.generator;

import pl.edu.agh.kis.model.DebugInfo;

public class GeneratorException extends RuntimeException {
    public enum ExceptionType {
        TEMPLATE_FILE_ERROR,
        PROGRAM_NOT_FOUND,
        STATEMENT_NOT_FOUND,
        EXPRESSION_NOT_FOUND,
        UNSUPPORTED_PATTERN
    }

    public ExceptionType type;
    public DebugInfo debugInfo;

    public static GeneratorException of(ExceptionType type, DebugInfo debugInfo) {
        String message = "";
        switch (type) {
            case TEMPLATE_FILE_ERROR -> message = String.format("Target config error: %s",
                    debugInfo.get("message"));
            case PROGRAM_NOT_FOUND -> message = String.format("Target '%s' does not define 'Program(methods, mainBody, includes, notice)'.",
                    debugInfo.get("target"));
            case STATEMENT_NOT_FOUND -> message = String.format("Target '%s' does not define 'AtomicSt(name, args)'.",
                    debugInfo.get("target"));
            case EXPRESSION_NOT_FOUND -> message = String.format("Target '%s' does not define 'AtomicExp(name, args)'.",
                    debugInfo.get("target"));
            case UNSUPPORTED_PATTERN -> message = String.format("(%d, %d): Target '%s' does not support pattern '%s'.",
                    debugInfo.startLine, debugInfo.startCharacter, debugInfo.get("target"), debugInfo.get("pattern"));
        }

        return new GeneratorException(type, message, debugInfo);
    }

    private GeneratorException(ExceptionType type, String message, DebugInfo debugInfo) {
        super(message);
        this.type = type;
        this.debugInfo = debugInfo;
    }
}
