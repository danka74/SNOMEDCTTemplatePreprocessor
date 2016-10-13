#!/bin/sh

PARSER=/home/danka74/git/SnomedCTParser/target/SnomedCTParser-0.4-SNAPSHOT-jar-with-dependencies.jar


echo "Translate to OWL"
java -jar $PARSER -l -f owlf -n stated ./out.scg

echo "Generate signature"
java -cp $PARSER se.liu.imt.mi.snomedct.expression.tools.SNOMEDCTSignatureGenerator -f \| ./out.scg