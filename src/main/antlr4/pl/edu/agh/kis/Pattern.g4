grammar Pattern;

/**
    Parser Grammar
*/

pattern :
    patternName LEFT_BRACKET (args+=pattern (DELIMITER args+=pattern)*)? RIGHT_BRACKET {
        int expected_arity = $patternName.ctx.arity;
        int got_arity = $args.size();

        if (expected_arity != got_arity) {
            throw GeneratorException.argumentCountMismatch($start.getLine(), $start.getCharPositionInLine(), $patternName.text, expected_arity, got_arity);
        }
    }
    | method
    | STRING
    ;

patternName
    locals [
        int arity = 0;
    ]
    : PATTERN_NAME_LEX {
        $arity = switch ($text) {
            case "Seq" -> 2;
            case "Branch", "Concur", "SeqSeq" -> 3;
            case "Cond", "If", "Para", "Loop", "Repeat" -> 4;
            default -> throw GeneratorException.unknownPattern($PATTERN_NAME_LEX.line, $PATTERN_NAME_LEX.pos, $text);
        };
    }
    ;

method : ATOM LEFT_BRACKET (args+=parameter (DELIMITER args+=parameter)*)? RIGHT_BRACKET (COLON returnType=STRING)?
    ;

parameter : INTEGER | FLOATING | BOOLEAN | STRING | method | typedExpr
    ;

typedExpr : expr=STRING COLON type=STRING
    ;

/**
    Lexer Grammar
*/

PATTERN_NAME_LEX: [A-Z][A-Za-z]*
    ;

DELIMITER: ','
    ;

LEFT_BRACKET: '('
    ;

RIGHT_BRACKET: ')'
    ;

COLON: ':'
    ;

BOOLEAN: 'true' | 'false'
    ;

ATOM: [a-z][A-Za-z0-9_]*
    ;

INTEGER : ('+' | '-') ? ('0' | [1-9][0-9]*)
    ;

FLOATING : ('+' | '-') ? ('0' | [1-9][0-9]*) '.' [0-9]+
    ;

STRING: '"' ('\\"'|.)*? '"'
    ;

WHITESPACE: [\r\n\t ]+ -> skip
    ;