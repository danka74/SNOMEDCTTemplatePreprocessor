/**
 * 
 */
package se.liu.imt.mi.snomedct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	public static List<HashMap<String, String>> readCSV(File input) {

		List<HashMap<String, String>> table = new LinkedList<HashMap<String, String>>();

		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(
					input));

			// read headers
			String headers[] = inputReader.readLine().split("\t");

			String line;
			while ((line = inputReader.readLine()) != null) {
				String[] items = line.split("\t");

				HashMap<String, String> set = new HashMap<String, String>();
				int length = items.length;
				for (int i = 0; i < length; i++) {
					String[] parts = items[i].split(";");
					for (int j = 0; j < parts.length; j++)
						if (!parts[j].isEmpty())
							set.put(headers[i], parts[j]);
				}

				table.add(set);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return table;

	}

	/**
	 * @param args[1]	a template file
	 * @param args[2]	a tab separated file with variable names in the first line
	 * 
	 * outputs result of template instatiation to System.out
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if(args.length != 2)
			return;
		
		File templateFile = new File(args[0]);
		File dataFile = new File(args[1]);
		
		// read data from dataFile
		List<HashMap<String, String>> data = readCSV(dataFile);
		
		// read entire templateFile into template string
		String template = new Scanner(templateFile).useDelimiter("\\Z").next();

		// for all rows in the data file
		for (HashMap<String, String> set : data) {

			StringReader reader = new StringReader(template);

			System.out.println("//\n// INPUT: " + set);

			try {
				// create stack of StringBuilder for the different scope blocks
				// in the template
				Deque<StringBuilder> scopeBlockTextStack = new LinkedList<StringBuilder>();

				// push a new StringBuilder for the outermost block
				scopeBlockTextStack.push(new StringBuilder());

				int level = 0; // keep track of level, for debugging mostly...
				
				// read one character
				int character = reader.read();
				while (true) {
					if (character == -1) // end of file
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
								if (!cardinality.getChild(3).getText()
										.equals("*"))
									max = Integer.parseInt(cardinality
											.getChild(3).getText()); // max is
																		// child
																		// no. 3
							}

							// look up value(s) of variables/paths
							// TODO: stub
							String value = set.get(variableName);

							int noOfValues = value == null ? 0 : 1;

							// check value(s) against constraints
							// TODO: stub
							// TODO: add meaning constraints
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
										if (j == slotIndex) // this is the slot
															// corresponding to
															// this scope:
															// replace the slot
															// with the value
											scopeBlockTextStack.peek().append(
													value);
										else {
											// else append the original text
											scopeBlockTextStack.peek().append(
													tree.getChild(j).getText());
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

					// read next character
					character = reader.read();
				}

				if (level != 0)
					throw new Exception("Scope end tag missing");

				String output = scopeBlockTextStack.peek().toString();

				output = cleanUpCommas(output);

				System.out.println(output);
			} catch (Exception e) {
				System.out.println("// ERROR: " + e.toString());
			}
			System.out.println();
		}
	}

	private static String cleanUpCommas(String str) {

		StringBuilder sb = new StringBuilder();
		int i = 0;
		int length = str.length();

		while (i < length) {
			char c = str.charAt(i);
			// if it's a comma or colon, check that next char, optionally after white
			// spaces, isn't a ')' or a '}'
			if (c == ',' || c == ':') {
				int j = i + 1;
				while (j < length) {
					char d = str.charAt(j);
					if (d == ' ' || d == '\n' || d == '\r' || d == '\t') {
						j++;
						continue;
					}
					if (d == ')' || d == '}') {
						i++;
						c = str.charAt(i);
						break;
					}
					break;
				}
				sb.append(c);
			} else
				sb.append(c);
			i++;
		}

		return sb.toString();
	}

}
