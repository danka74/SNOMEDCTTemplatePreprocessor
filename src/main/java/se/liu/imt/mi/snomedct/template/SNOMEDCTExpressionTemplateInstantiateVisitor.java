package se.liu.imt.mi.snomedct.template;

import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.collections4.MultiValuedMap;

import se.liu.imt.mi.snomedct.template.Slot;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateBaseVisitor;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeGroupContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeSetContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.ConceptReferenceContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.FocusConceptContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SlotContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SubExpressionContext;

public class SNOMEDCTExpressionTemplateInstantiateVisitor extends SNOMEDCTExpressionTemplateBaseVisitor<String> {

	private MultiValuedMap<ParserRuleContext, Slot> slotMap;
	private Map<String, String> data;

	public SNOMEDCTExpressionTemplateInstantiateVisitor(MultiValuedMap<ParserRuleContext, Slot> map,
			Map<String, String> data) {
		super();

		this.slotMap = map;
		this.data = data;
	}

	@Override
	public String visitFocusConcept(FocusConceptContext ctx) {

		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (ConceptReferenceContext conceptCtx : ctx.getRuleContexts(ConceptReferenceContext.class)) {
			SlotContext slotCtx = conceptCtx.getChild(SlotContext.class, 0);
			if (slotCtx != null) {
				// there can be many slots in for one list of focus concepts
				for (Slot slot : slotMap.get(conceptCtx)) {
					if (slot.getParseRuleContext() == conceptCtx) {
						String[] values = data.get(slot.getName()).split(";");
						if (values.length > slot.getCardinalityMax() || values.length < slot.getCardinalityMin())
							throw new ParseCancellationException("Cardinality error: " + values.length
									+ " values not allowed for slot @" + slot.getName());
						for (String val : values) {
							if (first) {
								first = false;
								builder.append(val); // or
														// visit(conceptCtx)
							} else {
								builder.append("+");
								builder.append(val);
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

	@Override
	public String visitAttributeGroup(AttributeGroupContext ctx) {
		// TODO Auto-generated method stub
		return new String("{").concat(visit(ctx.attributeSet())).concat("}");
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
		return new String("");
	}

	@Override
	public String visitSubExpression(SubExpressionContext ctx) {
		return visit(ctx.focusConcept()).concat(":").concat(visit(ctx.refinement()));
	}

	@Override
	public String visitAttributeSet(AttributeSetContext ctx) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (AttributeContext attrCtx : ctx.attribute()) {
			if (slotMap.containsKey(attrCtx)) {
				Slot attrNameSlot = null;
				Slot attrValueSlot = null;
				for (Slot slot : slotMap.get(attrCtx))
					if (slot.getPosition() == Slot.POSITION_ATTRIBUTE_NAME)
						attrNameSlot = slot;
					else if (slot.getPosition() == Slot.POSITION_ATTRIBUTE_VALUE)
						attrValueSlot = slot;
				
				

			} else if (first) {
				first = false;
				builder.append(visit(attrCtx));
			} else {
				builder.append(",");
				builder.append(visit(attrCtx));
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
