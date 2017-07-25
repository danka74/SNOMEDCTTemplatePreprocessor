/**
 * 
 */
package se.liu.imt.mi.snomedct.template;

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
import org.json.JSONObject;
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

		String dataString = "{\"rows\": ["
				+ "{\"bone\": [\"23416004 | Bone structure of ulna (body structure) |\",\"62413002 | Bone structure of radius (body structure) |\"],"
				+ "\"morph\": [\"72704001 |Fracture (morphologic abnormality)|\",\"108369006 | Neoplasm (morphologic abnormality) |\"]}"
				+ "]}";

		String testExpression = "64572001 | Disease (disorder) |: { 363698007 |Finding site (attribute)| = [[ scope=GROUP [1..*] @bone ]], 116676008 |Associated morphology (attribute)| = [[ scope=GROUP [1..*] @morph ]]  }";

		CodePointCharStream cs = CharStreams.fromString(testExpression);
		SNOMEDCTExpressionTemplateLexer lexer = new SNOMEDCTExpressionTemplateLexer(
				cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SNOMEDCTExpressionTemplateParser parser = new SNOMEDCTExpressionTemplateParser(
				tokens);
		ParseTree tree = null;
		tree = parser.expression();
		SNOMEDCTExpressionTemplateSetupVisitor setUpVisitor = new SNOMEDCTExpressionTemplateSetupVisitor();
		setUpVisitor.visit(tree);

		TemplateData td = TemplateData.readJSON(new StringReader(dataString));
		for (Object row : td) {
			SNOMEDCTExpressionTemplateInstantiateVisitor visitor = new SNOMEDCTExpressionTemplateInstantiateVisitor(
					setUpVisitor.getSlotMap(), (JSONObject) row);
			String res = visitor.visit(tree);

			System.out.println(res);

		}

		assert (setUpVisitor.getSlotMap().size() == 6);

	}
}
