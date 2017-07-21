parser grammar SlotParser;

options { tokenVocab=SlotLexer; }

scope: TEXT? (slot TEXT?)+;

slot: SLOT_START cardinality? variable? SLOT_END;

variable: VARIABLE;

cardinality: LBRACKET NUM DOTDOT (NUM | STAR) RBRACKET;