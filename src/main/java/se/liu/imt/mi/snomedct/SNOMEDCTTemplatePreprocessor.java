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
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import se.liu.imt.mi.snomedct.expression.SlotLexer;
import se.liu.imt.mi.snomedct.expression.SlotParser;
import se.liu.imt.mi.snomedct.expression.SlotParser.CardinalityContext;
import se.liu.imt.mi.snomedct.expression.SlotParser.SlotContext;
import se.liu.imt.mi.snomedct.expression.SlotParser.VariableContext;
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

		HashMap<String, String> set1 = new HashMap<String, String>();
		set1.put("variable1", "658778|concept2|");
		set1.put("variable2", "84758475|concept3|");
		variableLUT.add(set1);

		String[] examples = {
				"123567|concept1|#<:{ 847857|attribute1|= [[ @variableX ]]#<, 823781|attribute2|= [[ [1..1] @variable2 ]] #>}#>", // variableX
																																	// not
																																	// present
				"123567|concept1|:#<#<{ 847857|attribute1|= [[ @variable1 ]], 823781|attribute2|= [[ @variable2 ]]#>}#>",
				"123567|concept1|:#<{ 847857|attribute1|= [[ @variable1 ]]#<, 823781|attribute2|= [[ @variable2 ]]#>}#>",
				"123567|concept1|:#<{ 847857|attribute1|= [[ @variable1 ]]#<, 823781|attribute2|= [[ [0..0] @variable2 ]]#>}#>", // wrong
																																	// cardinality
				"123567|concept1|:#<{ 847857|attribute1|= [[ @variable1 ]], 823781|attribute2|= [[ @variable2 ]]#>}#>", // missing
																														// scope
																														// start
				"123567|concept1|:#<#<{ 847857|attribute1|= [[ @variable1 ]], 823781|attribute2|= @variable2 #>}#>", // missing
																														// slot
				"123567|concept1|:#<{ 847857|attribute1|= [[ @variable1 ]]#<, 823781|attribute2|= [[ @variableX ]]#>}#>" }; // variableX
																															// not
																															// present

		for (String example : examples) {

			StringReader reader = new StringReader(example);

			System.out.print(example);
			System.out.print(" - ");

			try {
				// create stack of StringBuilder for the different scope blocks
				// in
				// the template
				Deque<StringBuilder> scopeBlockTextStack = new LinkedList<StringBuilder>();

				// push a new StringBuilder for the outermost block
				scopeBlockTextStack.push(new StringBuilder());

				int level = 0; // keep track of level, for debugging mostly...
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
							// pop the StringBuilder corresponding to the scope
							// that
							// just ended
							StringBuilder sb = scopeBlockTextStack.pop();
							// get string including everything between but not
							// including
							// scope markers
							String scopeString = sb.toString();

							// parse the string contents of the scope
							ANTLRInputStream is = new ANTLRInputStream(
									scopeString);
							SlotLexer lexer = new SlotLexer(is);
							CommonTokenStream tokens = new CommonTokenStream(
									lexer);
							SlotParser parser = new SlotParser(tokens);
							parser.setErrorHandler(new BailErrorStrategy());
							// the parse tree should contain 1..* slots ([[ ...
							// ]])
							// possibly surrounded by arbitrary character
							// content
							ParseTree tree = null;
							try {
								tree = parser.scope();
							} catch (ParseCancellationException pce) {
								throw new Exception(pce.getCause());
							}

							// get the slot corresponding to the current scope
							// by
							// selecting the last slot occurring within the
							// scope
							SlotContext slot = null;
							int slotIndex = -1;
							if (tree.getChildCount() > 0)
								for (int i = tree.getChildCount() - 1; i >= 0; i--) {
									if (tree.getChild(i).getClass() == SlotContext.class) {
										slot = (SlotContext) tree.getChild(i);
										slotIndex = i;
										break;
									}
								}
							if (slot == null || slotIndex == -1)
								throw new Exception("No slot in the scope");

							// extract variable name/slot path
							VariableContext variable = slot.getChild(
									VariableContext.class, 0);
							String variableName = variable.getText().substring(
									1);

							// PathContext path =
							// slot.getChild(VariableContext.class,
							// 0);

							CardinalityContext cardinality = slot.getChild(
									CardinalityContext.class, 0);
							int min = 0;
							int max = Integer.MAX_VALUE;
							if (cardinality != null) {
								min = Integer.parseInt(cardinality.getChild(1)
										.getText()); // min is child no. 1
								max = Integer.parseInt(cardinality.getChild(3)
										.getText()); // max is child no. 3
							}

							// look up value(s) of variables/paths
							// TODO: stub
							String value = set1.get(variableName);

							int noOfValues = 1;

							// check value(s) against constraints
							// TODO: stub
							if (noOfValues < min || noOfValues > max)
								throw new Exception(
										"Cardinality constraint broken: "
												+ variableName);

							// for each value, output everything but the current
							// slot
							// which is replace by value
							// TODO: check when commas needs to be added
							if (value != null)
								for (int i = 0; i < noOfValues; i++) {
									for (int j = 0; j < tree.getChildCount(); j++) {
										if (j != slotIndex) // tree.getChild(j).getClass()
															// !=
															// SlotContext.class)
											scopeBlockTextStack.peek().append(
													tree.getChild(j).getText());
										else {
											scopeBlockTextStack.peek().append(
													value);
										}
									}
								}

							level--;

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
						scopeBlockTextStack.peek().append((char) prevCharacter);
						continue;
					default:
						// append the read character to the current
						// StringBuilder
						scopeBlockTextStack.peek().append((char) character);
					}

					character = reader.read();
				}

				System.out.println(scopeBlockTextStack.peek().toString());
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

}
