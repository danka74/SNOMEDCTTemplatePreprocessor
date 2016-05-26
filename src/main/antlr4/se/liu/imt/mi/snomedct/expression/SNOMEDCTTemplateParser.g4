parser grammar SNOMEDCTTemplateParser;

options {
	tokenVocab = SNOMEDCTTemplateLexer;
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
	slot
	| SCTID
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

slot
:
	SLOT_START scope? cardinality? variable? SLOT_END
;

scope
:
	SCOPE
;

variable
:
	VARIABLE
;

cardinality
:
	LBRACKET NUM DOTDOT
	(
		NUM
		| STAR
	) RBRACKET
;