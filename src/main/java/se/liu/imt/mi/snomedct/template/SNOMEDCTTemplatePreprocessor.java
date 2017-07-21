/**
 * 
 */
package se.liu.imt.mi.snomedct.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import se.liu.imt.mi.snomedct.template.SlotLexer;
import se.liu.imt.mi.snomedct.template.SlotParser;
import se.liu.imt.mi.snomedct.template.SlotParser.CardinalityContext;
import se.liu.imt.mi.snomedct.template.SlotParser.SlotContext;
import se.liu.imt.mi.snomedct.template.SlotParser.VariableContext;

/**
 * @author danka74
 *
 */
public class SNOMEDCTTemplatePreprocessor {

	public static CSVParser readCSV(File input) throws IOException {

		CSVParser parser = CSVParser.parse(input, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader());

		return parser;

	}

	public static String instantiate(String template, CSVParser data) {

		StringWriter writer = new StringWriter();

		// for all rows in the data file
		for (CSVRecord set : data) {

			StringReader reader = new StringReader(template);

			writer.write("//\n// INPUT: " + set.toString() + "\n");

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
								throw new Exception("Scope block end without preceding start");
							// pop the StringBuilder corresponding to the scope
							// that
							// just ended
							StringBuilder sb = scopeBlockTextStack.pop();
							// get string including everything between but not
							// including
							// scope markers
							String scopeString = sb.toString();

							// parse the string contents of the scope
							ANTLRInputStream is = new ANTLRInputStream(scopeString);
							SlotLexer lexer = new SlotLexer(is);
							CommonTokenStream tokens = new CommonTokenStream(lexer);
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
							VariableContext variable = slot.getChild(VariableContext.class, 0);
							String variableName = variable.getText().substring(1);

							// PathContext path =
							// slot.getChild(VariableContext.class,
							// 0);

							CardinalityContext cardinality = slot.getChild(CardinalityContext.class, 0);
							int min = 0;
							int max = Integer.MAX_VALUE;
							if (cardinality != null) {
								min = Integer.parseInt(cardinality.getChild(1).getText()); // min
																							// is
																							// child
																							// no.
																							// 1
								if (!cardinality.getChild(3).getText().equals("*"))
									max = Integer.parseInt(cardinality.getChild(3).getText()); // max
																								// is
								// child
								// no. 3
							}

							// look up value(s) of variables/paths
							// TODO: stub

							List<String> values = new ArrayList<>();
							String valueString = set.get(variableName);
							if(valueString.length() > 0)
								for (String val : valueString.split(";")) {
									values.add(val);
								}

							int noOfValues = values.size();

							// check value(s) against constraints
							// TODO: stub
							// TODO: add meaning constraints
							if (noOfValues < min || noOfValues > max)
								throw new Exception("Cardinality constraint broken: " + variableName);

							// for each value, output everything but the current
							// slot
							// which is replace by value
							// TODO: check when commas needs to be added
							for (int i = 0; i < noOfValues; i++) {
								for (int j = 0; j < tree.getChildCount(); j++) {
									if (j == slotIndex) // this is the slot
										// corresponding to
										// this scope:
										// replace the slot
										// with the value
										scopeBlockTextStack.peek().append(values.get(i));
									else {
										// else append the original text
										scopeBlockTextStack.peek().append(tree.getChild(j).getText());
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

				writer.write(output);
				writer.write('\n');
			} catch (Exception e) {
				writer.write("// ERROR: " + e.toString() + "\n");
			}
			writer.write('\n');
		}

		return writer.toString();

	}

	/**
	 * @param args[1]
	 *            a template file
	 * @param args[2]
	 *            a tab separated file with variable names in the first line
	 * 
	 *            outputs result of template instatiation to System.out
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 2)
			return;

		File templateFile = new File(args[0]);
		File dataFile = new File(args[1]);

		// read data from dataFile
		CSVParser data = readCSV(dataFile);

		// read entire templateFile into template string
		Scanner scan = new Scanner(templateFile);
		String template = scan.useDelimiter("\\Z").next();
		scan.close();

		String result = instantiate(template, data);

		System.out.println(result);

	}

	private static String cleanUpCommas(String str) {

		StringBuilder sb = new StringBuilder();
		int i = 0;
		int length = str.length();

		while (i < length) {
			char c = str.charAt(i);
			// if it's a plus, comma or colon, check that next char, optionally after
			// white
			// spaces, isn't a ')' or a '}'
			if (c == '+' || c == ',' || c == ':') {
				int j = i + 1;
				while (j < length) {
					char d = str.charAt(j);
					if (d == ' ' || d == '\n' || d == '\r' || d == '\t') {
						j++;
						continue;
					}
					if (d == ')' || d == '}' || d == ':') {
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
