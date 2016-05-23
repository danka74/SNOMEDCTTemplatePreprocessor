/**
 * 
 */
package se.liu.imt.mi.snomedct;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import se.liu.imt.mi.snomedct.expression.SlotLexer;
import se.liu.imt.mi.snomedct.expression.SlotParser;
import se.liu.imt.mi.snomedct.expression.SlotParserBaseVisitor;

/**
 * @author danka74
 *
 */
public class SNOMEDCTTemplatePreprocessor {

	/**
	 * 
	 */
	public SNOMEDCTTemplatePreprocessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		List<HashMap<String, String>> variableLUT = new LinkedList<HashMap<String, String>>();

		HashMap set1 = new HashMap();
		set1.put("variable1", "658778|concept2|");
		set1.put("variable2", "84758475|concept3|");
		variableLUT.add(set1);

		Reader reader = new StringReader(
				"123567|concept1|:#<#< 847857|attribute1|= [[ @variable1 ]], 823781|attribute2|= [[ @variable2 ]]#>#>");

		Writer writer = new StringWriter();

		// create stack of StringBuilder for the different scope blocks in the
		// template
		Deque<StringBuilder> scopeBlockTextStack = new LinkedList<StringBuilder>();

		// push a new StringBuilder for the outermost block
		scopeBlockTextStack.push(new StringBuilder());

		int level = 0;
		int character = reader.read();
		while (true) {
			if (character == -1)
				break;

			switch (character) {
			case '#': 
				// identify end of scope block '#>'
				character = reader.read(); // look at next character
				int prevCharacter = character;
				if (character == '>') {
					if (scopeBlockTextStack.size() == 1)
						throw new Exception(
								"Scope block end without preceding start");
					level--;
					// pop the current StringBuilder and append it to the next
					// StringBuilder down the stack
					StringBuilder sb = scopeBlockTextStack.pop();
					
					String scopeString = sb.toString();
					
					ANTLRInputStream is = new ANTLRInputStream(scopeString);
					SlotLexer lexer = new SlotLexer(is);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
					SlotParser parser = new SlotParser(tokens);
					
					ParseTree tree = parser.file();
					
					SlotParserBaseVisitor<String> visitor = new SlotParserBaseVisitor<String>();
					visitor.visit(tree);
					
					scopeBlockTextStack.peek().append(sb);
					break;
				} else 
					// identify scope start '#<'
					if (character == '<') {
					// push a new Stringbuilder for the new scope block
					level++;
					scopeBlockTextStack.push(new StringBuilder());
					break;
				}
				// write the consumed character and continue
				scopeBlockTextStack.peek().append((char)prevCharacter);
				continue;
			default:
				// append the read character to the current StringBuilder
				scopeBlockTextStack.peek().append((char)character);
			}

			character = reader.read();
		}

		System.out.println(scopeBlockTextStack.peek().toString());
	}

}
