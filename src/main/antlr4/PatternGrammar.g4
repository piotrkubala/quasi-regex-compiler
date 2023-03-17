grammar PatternGrammar;

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
        switch($text) {
        case "Seq":
            $number_of_args = 2;
            break;
        case "Branch":
            $number_of_args = 3;
            break;
        case "BranchRe":
            $number_of_args = 3;
            break;
        case "Concur":
            $number_of_args = 3;
            break;
        case "ConcurRe":
            $number_of_args = 3;
            break;
        case "Cond":
            $number_of_args = 4;
            break;
        case "Para":
            $number_of_args = 4;
            break;
        case "Loop":
            $number_of_args = 4;
            break;
        case "Choice":
            $number_of_args = 4;
            break;
        case "SeqSeq":
            $number_of_args = 3;
            break;
        case "Repeat":
            $number_of_args = 4;
            break;
        default:
            throw new RuntimeException("Unknown pattern name: ");
        }
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