grammar Pattern;

/**
    Parser Grammar
*/

pattern:
    pattern_name LEFT_BRACKET arguments RIGHT_BRACKET {
        int expected_number = $pattern_name.ctx.number_of_args;
        int number_got = $arguments.ctx.number_of_args;
        String res = "Expected " + expected_number + " of arguments but got " + number_got;

        if (expected_number != number_got) {
            // maybe throw an exception here?
            System.out.println(res);
            throw new RuntimeException(res);
        }
    }
    | ATOM
    ;

pattern_name
    locals [
        int number_of_args = 0;
    ]
    : PATTERN_NAME_LEX {
        $number_of_args = switch($text) {
            case "Seq" -> 2;
            case "Branch", "Concur", "SeqSeq" -> 3;
            case "Cond", "If", "Para", "Loop", "Repeat" -> 4;
            default -> -1;
        };
    }
    ;

args_with_delim
    returns [int number_of_args]
    @init {
        int number_of_args = 0;
    }
    : DELIMITER arguments {$number_of_args = $arguments.number_of_args + 1;}
    ;

arguments
    returns [int number_of_args]
    @init {
        int number_of_args = 0;
    }
    : pattern {$number_of_args = 1;}
    | pattern args_with_delim {$number_of_args = $args_with_delim.number_of_args;}
    | args_with_delim pattern {$number_of_args = $args_with_delim.number_of_args;}
    ;


/**
    Lexer Grammar
*/

PATTERN_NAME_LEX: [A-Z][a-z]*
    ;

DELIMITER: ','
    ;

LEFT_BRACKET: '('
    ;
RIGHT_BRACKET: ')'
    ;

ATOM: [a-z]+
    ;

WHITESPACE: [\n\t ]+ -> skip
    ;