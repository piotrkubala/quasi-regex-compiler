grammar Pattern;

/**
    Parser Grammar
*/

pattern :
    patternName LEFT_BRACKET (args+=pattern (DELIMITER args+=pattern)*)? RIGHT_BRACKET
    | ATOM
    | method
    | STRING
    | EMPTY
    ;

patternName : PATTERN_NAME_LEX
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

EMPTY: 'empty'
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