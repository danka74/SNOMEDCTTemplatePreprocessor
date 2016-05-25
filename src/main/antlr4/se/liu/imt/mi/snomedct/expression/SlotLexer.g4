lexer grammar SlotLexer;

SLOT_START: '[[' -> mode(ISLAND);
TEXT: ~[\[]+;

mode ISLAND;
SLOT_END: ']]' -> mode(DEFAULT_MODE);
ID: [A-Za-z0-9]+;
WS: [ \n\t\r]+ -> skip;
AT: '@';
CARDINALITY: '1' | [10] '..' [01\*];
OP: '<' | '<<';