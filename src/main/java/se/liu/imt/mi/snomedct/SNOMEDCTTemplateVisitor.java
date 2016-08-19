package se.liu.imt.mi.snomedct;

import java.util.HashMap;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.log4j.Logger;

import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.AttributeContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.AttributeGroupContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.AttributeSetContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.AttributeValueContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.CardinalityContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.ConceptReferenceContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.DefinitionStatusContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.ExpressionContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.FocusConceptContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.NestedExpressionContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.NonGroupedAttributeSetContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.RefinementContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.ScopeContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.SlotContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.StatementContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.StatementsContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.SubExpressionContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.VariableContext;
import se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor;

public class SNOMEDCTTemplateVisitor extends
		SNOMEDCTTemplateParserBaseVisitor<String> {

	private HashMap<String, String> variableLUT;

	public SNOMEDCTTemplateVisitor(HashMap<String, String> variableLUT) {
		super();
		this.variableLUT = variableLUT;
	}

	final static Logger logger = Logger
			.getLogger(SNOMEDCTTemplateVisitor.class);

	@Override
	public String aggregateResult(String aggregate, String nextResult) {
		if (aggregate == null) {
			return nextResult;
		}

		if (nextResult == null) {
			return aggregate;
		}

		StringBuilder sb = new StringBuilder(aggregate);
		sb.append(nextResult);

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitStatements
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.
	 * StatementsContext )
	 */
	@Override
	public String visitStatements(StatementsContext ctx) {
		// TODO Auto-generated method stub
		return super.visitStatements(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitStatement
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.StatementContext
	 * )
	 */
	@Override
	public String visitStatement(StatementContext ctx) {
		// TODO Auto-generated method stub
		return super.visitStatement(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitExpression
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.
	 * ExpressionContext )
	 */
	@Override
	public String visitExpression(ExpressionContext ctx) {

		return super.visitExpression(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitDefinitionStatus
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .DefinitionStatusContext)
	 */
	@Override
	public String visitDefinitionStatus(DefinitionStatusContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDefinitionStatus(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitSubExpression
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .SubExpressionContext)
	 */
	@Override
	public String visitSubExpression(SubExpressionContext ctx) {
		logger.info("hejsan");
		return super.visitSubExpression(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitFocusConcept
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .FocusConceptContext)
	 */
	@Override
	public String visitFocusConcept(FocusConceptContext ctx) {
		// TODO Auto-generated method stub
		return super.visitFocusConcept(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitConceptReference
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .ConceptReferenceContext)
	 */
	@Override
	public String visitConceptReference(ConceptReferenceContext ctx) {
		// TODO Auto-generated method stub
		return super.visitConceptReference(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitRefinement
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.
	 * RefinementContext )
	 */
	@Override
	public String visitRefinement(RefinementContext ctx) {
		// TODO Auto-generated method stub
		return super.visitRefinement(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitAttributeGroup
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .AttributeGroupContext)
	 */
	@Override
	public String visitAttributeGroup(AttributeGroupContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAttributeGroup(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitNonGroupedAttributeSet
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .NonGroupedAttributeSetContext)
	 */
	@Override
	public String visitNonGroupedAttributeSet(NonGroupedAttributeSetContext ctx) {
		// TODO Auto-generated method stub
		return super.visitNonGroupedAttributeSet(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitAttributeSet
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .AttributeSetContext)
	 */
	@Override
	public String visitAttributeSet(AttributeSetContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAttributeSet(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitAttribute
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.AttributeContext
	 * )
	 */
	@Override
	public String visitAttribute(AttributeContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAttribute(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitAttributeValue
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .AttributeValueContext)
	 */
	@Override
	public String visitAttributeValue(AttributeValueContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAttributeValue(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitNestedExpression
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser
	 * .NestedExpressionContext)
	 */
	@Override
	public String visitNestedExpression(NestedExpressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitNestedExpression(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#visitSlot
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.SlotContext)
	 */
	@Override
	public String visitSlot(SlotContext ctx) {

		logger.info(ctx.getText());

		CardinalityContext cardinality = ctx.getChild(CardinalityContext.class,
				0);
		logger.info(cardinality.getText());
		int min = 0;
		int max = Integer.MAX_VALUE;
		if (cardinality != null) {
			min = Integer.parseInt(cardinality.getChild(1).getText()); // min is
																		// child
																		// no. 1
			if (!cardinality.getChild(3).getText().equals("*"))
				max = Integer.parseInt(cardinality.getChild(3).getText()); // max
																			// is
																			// child
																			// no.
																			// 3
		}

		// extract variable name/slot path
		VariableContext variable = ctx.getChild(VariableContext.class, 0);
		String variableName = variable.getText().substring(1);

		// look up value(s) of variables/paths
		// TODO: stub
		String value = variableLUT.get(variableName);

		int noOfValues = 1;

		// check value(s) against constraints
		// TODO: stub
		// TODO: add meaning constraints
		if (noOfValues < min || noOfValues > max)
			;
		
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.antlr.v4.runtime.tree.AbstractParseTreeVisitor#visitTerminal(org.
	 * antlr.v4.runtime.tree.TerminalNode)
	 */
	@Override
	public String visitTerminal(TerminalNode node) {
		return node.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitScope
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.ScopeContext)
	 */
	@Override
	public String visitScope(ScopeContext ctx) {
		// TODO Auto-generated method stub
		return super.visitScope(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitVariable
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.VariableContext
	 * )
	 */
	@Override
	public String visitVariable(VariableContext ctx) {
		// TODO Auto-generated method stub
		return super.visitVariable(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParserBaseVisitor#
	 * visitCardinality
	 * (se.liu.imt.mi.snomedct.expression.SNOMEDCTTemplateParser.
	 * CardinalityContext)
	 */
	@Override
	public String visitCardinality(CardinalityContext ctx) {
		// TODO Auto-generated method stub
		return super.visitCardinality(ctx);
	}

}
