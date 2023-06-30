package pl.edu.agh.kis.validation;

import pl.edu.agh.kis.model.DebugInfo;

public class ValidatorException extends RuntimeException {
    public enum ExceptionType {
        ARGUMENT_COUNT_MISMATCH,
        NON_BOOLEAN_PREDICATE,
        RETURN_PATTERN_MISMATCH
    }

    public ExceptionType type;
    public DebugInfo debugInfo;

    public static ValidatorException of(ExceptionType type, DebugInfo debugInfo) {
        String message = "";
        switch (type) {
            case ARGUMENT_COUNT_MISMATCH -> message = String.format("(%d, %d): Pattern '%s' expects %d arguments, but got %d.",
                    debugInfo.startLine, debugInfo.startCharacter, debugInfo.get("name"), (Integer) debugInfo.get("expected"), (Integer) debugInfo.get("got"));
            case NON_BOOLEAN_PREDICATE -> {
                Integer predicatePosition = (Integer) debugInfo.get("predicatePosition");
                String argPos = (predicatePosition + 1) + switch ((predicatePosition + 1) % 10) {
                    case 1 -> "st";
                    case 2 -> "nd";
                    case 3 -> "rd";
                    default -> "th";
                };

                message = String.format("(%d, %d): The %s argument of '%s' must be a boolean value, a boolean method or a raw string.",
                        debugInfo.startLine, debugInfo.startCharacter, argPos, debugInfo.get("name"));
            }
            case RETURN_PATTERN_MISMATCH -> message = String.format("(%d, %d): Pattern '%s' must appear with the corresponding pattern in sequence as Seq(%s(...), %s(...)).",
                    debugInfo.startLine, debugInfo.startCharacter, debugInfo.get("name"), debugInfo.get("corresponding"), debugInfo.get("name"));
        }

        return new ValidatorException(type, message, debugInfo);
    }

    private ValidatorException(ExceptionType type, String message, DebugInfo debugInfo) {
        super(message);
        this.type = type;
        this.debugInfo = debugInfo;
    }
}
