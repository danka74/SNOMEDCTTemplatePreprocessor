/*
 * Grammar created according to SNOMED CT Compositional Grammar v2.3.1
 * 
 * Potential differences to standard:
 * 	Negative definition of nonwsNonPipe and anyNonEscapedChar
 *  Allows negative SCTID 
 */
 grammar SNOMEDCTExpressionTemplate;

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

 VBAR
 :
 	'|'
 ;

 SLOT_START
 :
 	'[['
 ;

 SLOT_END
 :
 	']]'
 ;

 slot
 :
 	SLOT
 ;

 statement
 :
 	LPARAN subExpression RPARAN definitionStatus LPARAN subExpression RPARAN
 ;

 expression
 :
 	definitionStatus subExpression
 	| subExpression
 ;

 definitionStatus
 :
 	EQ_TO
 	| SC_OF
 	| slot
 ;

 subExpression
 :
 	focusConcept
 	(
 		COLON refinement
 	)?
 ;

 focusConcept
 :
 	conceptReference
 	(
 		PLUS conceptReference
 	)*
 ;

 conceptReference
 :
 	SCTID
 	| SCTID TERM
 	| slot
 ;

 refinement
 :
 	nonGroupedAttributeSet
 	(
 		COMMA attributeGroup
 	)*
 	| attributeGroup
 	(
 		COMMA? attributeGroup
 	)*
 ;

 attributeGroup
 :
 	LCBRACKET attributeSet RCBRACKET
 ;

 nonGroupedAttributeSet
 :
 	attribute
 	(
 		COMMA attribute
 	)*
 ;

 attributeSet
 :
 	attribute
 	(
 		COMMA attribute
 	)*
 ;

 attribute
 :
 	attributeName EQ attributeValue
 ;
 
 attributeName
 :
 	conceptReference
 ;

 attributeValue
 :
 	conceptReference
 	| nestedExpression
 	| HASH_NUMBER
 	| STRING
 ;

 nestedExpression
 :
 	LPARAN subExpression RPARAN
 ;

 HASH_NUMBER
 :
 	'#0'
 	| '#' '-'? NONZERO DIGIT*
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

SLOT
:
	SLOT_START
	.*?
	SLOT_END
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
 	NONZERO DIGIT*
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


