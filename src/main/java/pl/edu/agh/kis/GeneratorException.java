package pl.edu.agh.kis;

public class GeneratorException extends RuntimeException {
    int line;
    int charPosition;

    public GeneratorException(int line, int charPosition, String message) {
        super(message);
        this.line = line;
        this.charPosition = charPosition;
    }

    public static GeneratorException argumentTypeMismatch(int line, int charPosition, int argumentPosition, String patternName) {
        String argPos = (argumentPosition + 1) + switch (argumentPosition + 1 % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        return new GeneratorException(line, charPosition,
                String.format("(%d, %d): The %s argument of '%s' must be an atomic method or a raw string.",
                        line, charPosition, argPos, patternName));
    }

    public static GeneratorException unknownPattern(int line, int charPosition, String patternName) {
        return new GeneratorException(line, charPosition,
                String.format("(%d, %d): Unknown pattern name '%s'.", line, charPosition, patternName));
    }

    public static GeneratorException argumentCountMismatch(int line, int charPosition, String patternName, int expected, int got) {
        return new GeneratorException(line, charPosition,
                String.format("(%d, %d): Pattern '%s' expects %d arguments, but got %d.",
                        line, charPosition, patternName, expected, got));
    }
}
