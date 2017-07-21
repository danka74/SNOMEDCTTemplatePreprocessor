grammar Slot2;

options {
	language = Java;
}

SLOT_START
:
	'[['
;

SLOT_END
:
	']]'
;

DOT_DOT
:
	'..'
;

LBRACKET
:
	'['
;

RBRACKET
:
	']'
;

STAR
:
	'*'
;

SCOPE
:
	[Ss] [Cc] [Oo] [Pp] [Ee]
;

PAIR
:
	[Pp] [Aa] [Ii] [Rr]
;

BLOCK_COMMENT
:
	'/*' .*? '*/' -> skip
;

EOL_COMMENT
:
	'//' ~[\r\n]* -> skip
;

innerSlot
:
	SLOT_START scope? pair? cardinality? slotName SLOT_END
;

scope
:
	SCOPE '=' SCOPES
;

pair
:
	PAIR '=' '(' VARIABLE+ ')'
;

cardinality
:
	LBRACKET NUM DOT_DOT
	(
		NUM
		| STAR
	) RBRACKET
;

slotName
:
	VARIABLE
;

NUM
:
	ZERO
	| NONZERO DIGIT*
;

SCOPES
:
	[Aa] [Tt] [Tt] [Rr] [Ii] [Bb] [Uu] [Tt] [Ee]
	| [Gg] [Rr] [Oo] [Uu] [Pp]
;

VARIABLE
:
	'@' LETTER
	(
		LETTER
		| DIGIT
	)*
;

fragment
LETTER
:
	[a-zA-Z\u0080-\u00FF_]
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
ZERO
:
	'0'
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