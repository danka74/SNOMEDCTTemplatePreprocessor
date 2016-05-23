parser grammar SlotParser;

options { tokenVocab=SlotLexer; }

file: TEXT slot TEXT;

slot: SLOT_START cardinality? variable? SLOT_END;

variable: AT ID;

cardinality: CARDINALITY;