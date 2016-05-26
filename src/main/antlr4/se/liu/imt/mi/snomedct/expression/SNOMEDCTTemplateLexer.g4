lexer grammar SNOMEDCTTemplateLexer;

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

SLOT_START
:
	'[[' -> mode ( ISLAND )
;

mode ISLAND;

WS_
:
	(
		'\t'
		| ' '
		| '\r'
		| '\n'
		| '\u000C'
	) -> skip
;

SLOT_END
:
	']]' -> mode ( DEFAULT_MODE )
;

NUM
:
	DIGIT+
	| '0'
;

VARIABLE
:
	AT ID
;

fragment
ID
:
	[A-Za-z0-9]+
;

fragment
AT
:
	'@'
;

DOTDOT
:
	'..'
;

STAR
:
	'*'
;

LBRACKET
:
	'['
;

RBRACKET
:
	']'
;

SCOPE
:
	'R'
	| 'G'
	| 'D'
	| 'T'
;