package se.liu.imt.mi.snomedct.template;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ParseConversionEvent;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.MultiValuedMap;
import org.json.JSONArray;
import org.json.JSONObject;

import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.NestedExpressionContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.RefinementContext;
import se.liu.imt.mi.snomedct.template.Slot;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateBaseVisitor;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeGroupContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeSetContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.ConceptReferenceContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.FocusConceptContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SlotContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SubExpressionContext;
import se.liu.imt.mi.snomedct.template.ValueIterator;

public class SNOMEDCTExpressionTemplateInstantiateVisitor2 extends
		SNOMEDCTExpressionTemplateBaseVisitor<String> {

	private MultiValuedMap<ParserRuleContext, Slot> slotMap;
	private JSONObject data;
	
	private Deque<StringBuilder> scopeBlockTextStack = new LinkedList<StringBuilder>();
	private int level = 0;


	public SNOMEDCTExpressionTemplateInstantiateVisitor2(
			MultiValuedMap<ParserRuleContext, Slot> map, JSONObject row) {
		super();

		this.slotMap = map;
		this.data = row;
	}

	@Override
	public String visitFocusConcept(FocusConceptContext ctx) {

		boolean first = true;

		for (ConceptReferenceContext conceptCtx : ctx
				.getRuleContexts(ConceptReferenceContext.class)) {
			scopeBlockTextStack.push(new StringBuilder());
			level++;
		}

		return builder.toString();
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {

		if (aggregate == null)
			return nextResult;

		if (nextResult == null)
			return aggregate;

		return aggregate.concat(nextResult);
	}
}
