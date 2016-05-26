lexer grammar SNOMEDCTExpressionLexer;

options {
	language = Java;
}

EQ_TO
:
	'==='
;

SC_OF
:
	'<<<'
;

LPARAN
:
	'('
;

RPARAN
:
	')'
;

COLON
:
	':'
;

PLUS
:
	'+'
;

COMMA
:
	','
;

EQ
:
	'='
;

LCBRACKET
:
	'{'
;

RCBRACKET
:
	'}'
;

BLOCK_COMMENT
:
	'/*' .*? '*/' -> skip
;

EOL_COMMENT
:
	'//' ~[\r\n]* -> skip
;

NUMBER
:
	'#' '-'? NONZERO DIGIT*
	(
		'.' DIGIT*
	)?
	| '#' '-'? '0.' DIGIT+
;

STRING
:
	'"'
	(
		ESCAPE_CHAR
		| ~( '"' | '\\' )
	)*? '"'
;

fragment
ESCAPE_CHAR
:
	'\\"'
	| '\\\\'
;

TERM
:
	'|' ' '* NONWSNONPIPE
	(
		' '
		| NONWSNONPIPE
	)* '|'
;

SCTID
:
	'-'? NONZERO
	(
		DIGIT
		| '-'
	)*
;

fragment
DIGIT
:
	'0' .. '9'
;

fragment
NONZERO
:
	'1' .. '9'
;

fragment
NONWSNONPIPE
:
	~( '|' | '\t' | ' ' | '\r' | '\n' | '\u000C' )
;

WS
:
	(
		'\t'
		| ' '
		| '\r'
		| '\n'
		| '\u000C'
	) -> skip
;


