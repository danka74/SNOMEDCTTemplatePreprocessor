lexer grammar SlotLexer;

SLOT_START
:
	'[[' -> mode ( ISLAND )
;

TEXT
:
	~[\[]+
;

mode ISLAND;

SLOT_END
:
	']]' -> mode ( DEFAULT_MODE )
;

fragment
DIGIT
:
	'0' .. '9'
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
	[A-Za-z0-9_]+
;

WS
:
	[ \n\t\r]+ -> skip
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

OP
:
	'<'
	| '<<'
; 