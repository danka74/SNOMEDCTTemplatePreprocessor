/**
 * 
 */
package se.liu.imt.mi.snomedct;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.spi.CharsetProvider;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateInstantiateVisitor;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateLexer;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser;
import se.liu.imt.mi.snomedct.template.Slot;
import se.liu.imt.mi.snomedct.template.TemplateData;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateSetupVisitor;

/**
 * @author danka74
 *
 */
public class TestSNOMEDCTExpressionTemplateInstantiateVisitor {

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
	public void test() throws IOException {

		String dataString = "findingWithExplicitContext\tassociatedFinding\tfindingSite\n" + "123\t345\t853\n" + "789\t456\t28347";

		String testExpression = "[[ [1..1] @findingWithExplicitContext ]]:\n"
				+ " { 246090004 |Associated finding| = ([[ [1..1] @associatedFinding ]]:\n"
				+ "{ 246112005 |Severity| = [[ [0..1] @severity]],\n"
				+ "363698007 |Finding site| = [[ scope=group [1..*] @findingSite]] } ) , \n"
				+ "408732007 |Subject relationship context| = 410604004 |Subject of record|,\n"
				+ "408731000 |Temporal context| = [[ [0..1] @temporalContext ]],\n"
				+ "408729009 |Finding context| = [[ [0..1] @findingContext ]] }";

		CodePointCharStream cs = CharStreams.fromString(testExpression);
		SNOMEDCTExpressionTemplateLexer lexer = new SNOMEDCTExpressionTemplateLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SNOMEDCTExpressionTemplateParser parser = new SNOMEDCTExpressionTemplateParser(tokens);
		ParseTree tree = null;
		tree = parser.expression();
		SNOMEDCTExpressionTemplateSetupVisitor setUpVisitor = new SNOMEDCTExpressionTemplateSetupVisitor();
		setUpVisitor.visit(tree);

		TemplateData data = TemplateData.readTDFS(new StringReader(dataString));
		for (Map<String, String> row : data) {
			SNOMEDCTExpressionTemplateInstantiateVisitor visitor = new SNOMEDCTExpressionTemplateInstantiateVisitor(
					setUpVisitor.getSlotMap(), row);
			String res = visitor.visit(tree);
			
			System.out.println(res);

		}

		assert (setUpVisitor.getSlotMap().size() == 6);
		
	}

}
