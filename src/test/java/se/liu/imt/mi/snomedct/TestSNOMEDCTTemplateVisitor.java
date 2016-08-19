/**
 * 
 */
package se.liu.imt.mi.snomedct;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateLexer;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser;
import se.liu.imt.mi.snomedct.expression.SlotLexer;
import se.liu.imt.mi.snomedct.expression.SlotParser;

/**
 * @author danka74
 *
 */
public class TestSNOMEDCTTemplateVisitor {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		HashMap<String, String> set1 = new HashMap<String, String>();
		set1.put("findingWithExplicitContext", "658778|concept2|");
		set1.put("associatedFinding", "84758475|concept3|");
		
		String testExpression = "[[ [1..1] @findingWithExplicitContext ]]:\n" + 
				" { 246090004 |Associated finding| = ([[ [0..1] @associatedFinding ]]:\n" + 
				"{ 246112005 |Severity| = [[ [0..1] @severity]],\n" + 
				"363698007 |Finding site| = [[ [0..1] @findingSite]] } ) , \n" + 
				"408732007 |Subject relationship context| = 410604004 |Subject of record|,\n" + 
				"408731000 |Temporal context| = [[ [1..1] @temporalContext ]],\n" + 
				"408729009 |Finding context| = [[ [1..1] @findingContext ]] }";

		ANTLRInputStream is = new ANTLRInputStream(testExpression);
		SNOMEDCTTemplateLexer lexer = new SNOMEDCTTemplateLexer(is);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SNOMEDCTTemplateParser parser = new SNOMEDCTTemplateParser(tokens);
		ParseTree tree = null;
		tree = parser.expression();
		SNOMEDCTTemplateVisitor visitor = new SNOMEDCTTemplateVisitor(set1);
		String result = visitor.visit(tree);
		
		System.out.println(result);

	}

}
