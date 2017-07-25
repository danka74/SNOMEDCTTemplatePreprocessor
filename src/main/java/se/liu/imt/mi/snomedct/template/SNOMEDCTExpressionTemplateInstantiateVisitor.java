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

public class SNOMEDCTExpressionTemplateInstantiateVisitor extends
		SNOMEDCTExpressionTemplateBaseVisitor<String> {

	private MultiValuedMap<ParserRuleContext, Slot> slotMap;
	private JSONObject data;

	public SNOMEDCTExpressionTemplateInstantiateVisitor(
			MultiValuedMap<ParserRuleContext, Slot> map, JSONObject row) {
		super();

		this.slotMap = map;
		this.data = row;
	}

	@Override
	public String visitFocusConcept(FocusConceptContext ctx) {

		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (ConceptReferenceContext conceptCtx : ctx
				.getRuleContexts(ConceptReferenceContext.class)) {
			SlotContext slotCtx = conceptCtx.getChild(SlotContext.class, 0);
			if (slotCtx != null) {
				// there can be many slots in for one list of focus concepts
				for (Slot slot : slotMap.get(conceptCtx)) {
					if (slot.getParseRuleContext() == conceptCtx) {
						ValueIterator i = TemplateData.getValueIterator(data,
								slot.getName());
						if (i.length() > slot.getCardinalityMax()
								|| i.length() < slot.getCardinalityMin())
							throw new ParseCancellationException(
									"Cardinality error: " + i.length()
											+ " values not allowed for slot @"
											+ slot.getName());
						while (i.hasNext()) {
							if (first) {
								first = false;
								builder.append(i.next()); // or
								// visit(conceptCtx)
							} else {
								builder.append("+");
								builder.append(i.next());
							}
						}
					}
				}

			} else {
				if (first) {
					first = false;
					builder.append(conceptCtx.getText()); // or
															// visit(conceptCtx)
				} else {
					builder.append("+");
					builder.append(conceptCtx.getText());
				}
			}
		}

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateBaseVisitor
	 * #visitNestedExpression
	 * (se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser
	 * .NestedExpressionContext)
	 */
	@Override
	public String visitNestedExpression(NestedExpressionContext ctx) {
		// TODO Auto-generated method stub
		return new String("(").concat(visit(ctx.subExpression())).concat(")");
	}

	@Override
	public String visitAttributeGroup(AttributeGroupContext ctx) {
		return new String("{").concat(visit(ctx.attributeSet())).concat("}");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateBaseVisitor
	 * #visitRefinement
	 * (se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser
	 * .RefinementContext)
	 */
	@Override
	public String visitRefinement(RefinementContext ctx) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		if (ctx.nonGroupedAttributeSet() != null) {
			String nonGrouped = visit(ctx.nonGroupedAttributeSet());
			if (nonGrouped.length() > 0) {
				builder.append(nonGrouped);
				first = false;
			}
		}

		for (AttributeGroupContext groupCtx : ctx.attributeGroup()) {
			String attrSet = visit(groupCtx.attributeSet());
			if (slotMap.containsKey(groupCtx)) {
				Collection<Slot> slots = slotMap.get(groupCtx);
				// get attribute contexts from slots
				HashSet<AttributeContext> attrCtxs = new HashSet<AttributeContext>();
				for(Slot slot : slots) {
					SlotContext slotCtx = slot.getSlotParseRuleContext();
					if
					attrCtxs.add((AttributeContext)slotCtx.getParent().getParent()); // add attribute contexts					
					
					ValueIterator i = TemplateData.getValueIterator(data, slot.getName());
					while(i.hasNext()) {
						StringBuilder slotBuilder = new StringBuilder();
						slotBuilder.append("{");
						slotBuilder.append(attrSet);
						
						slotBuilder.append("}");
						builder.append(slotBuilder);
					}
				}
			} else {
				if (!first)
					builder.append(",");
				builder.append(visit(groupCtx));
			}
		}

		return builder.toString();
	}

	@Override
	public String visitAttribute(AttributeContext ctx) {
		String name = visit(ctx.attributeName());
		String value = visit(ctx.attributeValue());
		return name.concat("=").concat(value);
	}

	@Override
	public String visitConceptReference(ConceptReferenceContext ctx) {
		SlotContext slotCtx = ctx.getChild(SlotContext.class, 0);
		if (slotCtx == null)
			return ctx.SCTID().getText().concat(ctx.TERM().getText());
		return new String("<slot>"); // should not happen!
	}

	@Override
	public String visitSubExpression(SubExpressionContext ctx) {
		return visit(ctx.focusConcept()).concat(":").concat(
				visit(ctx.refinement()));
	}

	@Override
	public String visitAttributeSet(AttributeSetContext ctx) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (AttributeContext attrCtx : ctx.attribute()) {
			if (slotMap.containsKey(attrCtx)) {
				ValueIterator nameIterator = null;
				ValueIterator valueIterator = null;
				for (Slot slot : slotMap.get(attrCtx))
					if (slot.getPosition() == Slot.POSITION_ATTRIBUTE_NAME) {
						nameIterator = TemplateData.getValueIterator(data,
								slot.getName());
						if (nameIterator.length() > slot.getCardinalityMax()
								|| nameIterator.length() < slot
										.getCardinalityMin())
							throw new ParseCancellationException(
									"Cardinality error: "
											+ nameIterator.length()
											+ " values not allowed for slot @"
											+ slot.getName());
					} else if (slot.getPosition() == Slot.POSITION_ATTRIBUTE_VALUE) {
						valueIterator = TemplateData.getValueIterator(data,
								slot.getName());
						if (valueIterator.length() > slot.getCardinalityMax()
								|| valueIterator.length() < slot
										.getCardinalityMin())
							throw new ParseCancellationException(
									"Cardinality error: "
											+ valueIterator.length()
											+ " values not allowed for slot @"
											+ slot.getName());
					}
				if (nameIterator != null) {
					while (nameIterator.hasNext()) {
						String nameValue = nameIterator.next();
						if (valueIterator != null) {
							while (valueIterator.hasNext()) {
								if (!first)
									builder.append(",");
								builder.append(nameValue).append("=")
										.append(valueIterator.next());
								first = false;
							}
						} else {
							if (!first)
								builder.append(",");
							builder.append(nameValue).append("=")
									.append(visit(attrCtx.attributeValue()));
							first = false;
						}

					}

				} else if (valueIterator != null) {
					while (valueIterator.hasNext()) {
						if (!first)
							builder.append(",");
						builder.append(visit(attrCtx.attributeName()))
								.append("=").append(valueIterator.next());
						first = false;
					}
				}
			} else {
				if (!first)
					builder.append(",");

				if (attrCtx.attributeName().conceptReference().slot() == null
						&& attrCtx.attributeValue().conceptReference().slot() == null) {

					builder.append(visit(attrCtx));
					first = false;
				}
			}
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
