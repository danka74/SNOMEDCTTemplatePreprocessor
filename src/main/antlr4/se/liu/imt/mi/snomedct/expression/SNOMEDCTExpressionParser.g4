parser grammar SNOMEDCTExpressionParser;

options {
	tokenVocab = SNOMEDCTExpressionLexer;
	language = Java;
}

statements
:
	statement
	(
		statement
	)*
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
;

refinement
:
	nonGroupedAttributeSet
	(
		COMMA attributeGroup
	)*
	| attributeGroup
	(
		COMMA attributeGroup
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
	attributeType = conceptReference EQ attributeValue
;

attributeValue
:
	conceptReference
	| nestedExpression
	| NUMBER
	| STRING
;

nestedExpression
:
	LPARAN subExpression RPARAN
;

