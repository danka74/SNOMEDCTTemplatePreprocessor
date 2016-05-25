parser grammar SlotParser;

options { tokenVocab=SlotLexer; }

scope: TEXT? (slot TEXT?)+;

slot: SLOT_START cardinality? variable? SLOT_END;

variable: AT ID;

cardinality: CARDINALITY;