/**
 * 
 */
package se.liu.imt.mi.snomedct.template;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SlotContext;
import se.liu.imt.mi.snomedct.template.Slot2Parser.SlotNameContext;
import se.liu.imt.mi.snomedct.template.SlotParser.VariableContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeGroupContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeNameContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.AttributeValueContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.DefinitionStatusContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.FocusConceptContext;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SubExpressionContext;
import se.liu.imt.mi.snomedct.template.Slot2Parser.CardinalityContext;
import se.liu.imt.mi.snomedct.template.Slot2Parser.InnerSlotContext;
import se.liu.imt.mi.snomedct.template.Slot2Parser.PairContext;
import se.liu.imt.mi.snomedct.template.Slot2Parser.ScopeContext;

/**
 * @author danka74
 *
 */
public class Slot {

	final static int CARD_STAR = Integer.MAX_VALUE;
	final static int SCOPE_IMMEDIATE = 1; // immediately surrounding attribute,
											// focus concept or definition
											// status
	final static int SCOPE_GROUP = 2;
	// final static int SCOPE_EXPRESSION = 3;
	final static int DEFAULT_SCOPE = SCOPE_IMMEDIATE;
	final static String[] SCOPE_NAMES = { "IMMEDIATE", "GROUP" };

	final static int DEFAULT_CARD_MIN = 0;
	final static int DEFAULT_CARD_MAX = 1;

	int cardinalityMin, cardinalityMax;
	int scope;
	int position;
	public int getPosition() {
		return position;
	}

	String constraint;
	String name;
	ParserRuleContext instantiateParseCtx;
	SlotContext slotParseCtx;
	Set<String> pairs;

	final static int POSITION_DEF_STATUS = 1;
	final static int POSITION_FOCUS_CONCEPT = 2;
	final static int POSITION_ATTRIBUTE_NAME = 3;
	final static int POSITION_ATTRIBUTE_VALUE = 4;

	/**
	 * @param innerSlotCtx
	 * @param outerSlotCtx
	 */
	Slot(InnerSlotContext innerSlotCtx, SlotContext outerSlotCtx) {

		this.slotParseCtx = outerSlotCtx;

		// find position in parse tree
		if (outerSlotCtx.getParent().getClass() == DefinitionStatusContext.class) {
			this.position = Slot.POSITION_DEF_STATUS;
		} else {
			// need to look 2 levels up, past conceptReference to determine
			// position
			ParserRuleContext ppCtx = outerSlotCtx.getParent().getParent();
			if (ppCtx.getClass() == FocusConceptContext.class) {
				this.position = Slot.POSITION_FOCUS_CONCEPT;
			} else if (ppCtx.getClass() == AttributeNameContext.class) {
				this.position = Slot.POSITION_ATTRIBUTE_NAME;
			} else if (ppCtx.getClass() == AttributeValueContext.class) {
				this.position = Slot.POSITION_ATTRIBUTE_VALUE;
			} else
				throw new ParseCancellationException("Cannot determine position");

		}

		// fetch slot name
		SlotNameContext nameCtx = innerSlotCtx.getChild(SlotNameContext.class, 0);
		if (nameCtx != null)
			this.name = nameCtx.getText().substring(1);
		else
			// should never happen as it wouldn't parse!
			throw new ParseCancellationException("No slot name in slot : " + innerSlotCtx.getText());

		// fetch slot cardinality
		CardinalityContext cardCtx = innerSlotCtx.getChild(CardinalityContext.class, 0);
		if (cardCtx != null) {
			this.cardinalityMin = Integer.parseInt(cardCtx.getChild(1).getText()); // min
																					// is
																					// child
																					// no.
																					// 1
			if (!cardCtx.getChild(3).getText().equals("*"))
				this.cardinalityMax = Integer.parseInt(cardCtx.getChild(3).getText()); // max
																						// is
																						// child
																						// no.
																						// 3
			else
				this.cardinalityMax = Slot.CARD_STAR;

		} else {
			this.cardinalityMin = Slot.DEFAULT_CARD_MIN;
			this.cardinalityMax = Slot.DEFAULT_CARD_MAX;
		}
		if (this.position == Slot.POSITION_DEF_STATUS && (this.cardinalityMax > 1 || this.cardinalityMin > 1))
			throw new ParseCancellationException("Slots for definitions status cannot have any cardinality > 1");

		// fetch slot scope
		ScopeContext scopeCtx = innerSlotCtx.getChild(ScopeContext.class, 0);
		if (scopeCtx != null) {
			String scopeName = scopeCtx.getChild(2).getText();
			int i = 1;
			for (String name : Slot.SCOPE_NAMES) {
				if (scopeName.equalsIgnoreCase(name)) {
					this.scope = i;
					break;
				}
				i++;
			}
		} else
			this.scope = Slot.DEFAULT_SCOPE;
		if (this.position == Slot.POSITION_DEF_STATUS && this.scope != Slot.SCOPE_IMMEDIATE)
			throw new ParseCancellationException("Slots for definitions status can only have immediate scope");
		if (this.position == Slot.POSITION_FOCUS_CONCEPT && this.scope != Slot.SCOPE_IMMEDIATE)
			throw new ParseCancellationException("Slots for focus concepts can only have immediate scope");

		// fetch pairs
		PairContext pairCtx = innerSlotCtx.getChild(PairContext.class, 0);
		if (pairCtx != null) {
			if (this.position == Slot.POSITION_DEF_STATUS)
				throw new ParseCancellationException("Slots for definition status cannot be paired");
			this.pairs = new HashSet<String>();
			for (VariableContext varCtx : pairCtx.getRuleContexts(VariableContext.class)) {
				this.pairs.add(varCtx.getText().substring(2));
			}
		}

		// assign parse context class. the parse context assigned is the context
		// which is removed or duplicated
		switch (this.position) {
		case Slot.POSITION_DEF_STATUS:
			this.instantiateParseCtx = outerSlotCtx.getParent(); // definitionStatus
			break;
		case Slot.POSITION_FOCUS_CONCEPT:
			this.instantiateParseCtx = outerSlotCtx.getParent(); // conceptReference
			break;
		case Slot.POSITION_ATTRIBUTE_NAME:
		case Slot.POSITION_ATTRIBUTE_VALUE:
			switch (this.scope) {
			case Slot.SCOPE_IMMEDIATE:
				this.instantiateParseCtx = outerSlotCtx.getParent().getParent().getParent(); // attribute
				break;
			case Slot.SCOPE_GROUP:
				// find group going up the parse tree
				ParserRuleContext parent = outerSlotCtx.getParent();
				while (parent != null) {
					if (parent.getClass() == SubExpressionContext.class)
						throw new ParseCancellationException("No surrounding group in (sub)expression");
					if (parent.getClass() == AttributeGroupContext.class) {
						this.instantiateParseCtx = parent;
						break;
					}
					parent = parent.getParent();
				}
				break;
			}
			break;
		default:
			throw new ParseCancellationException();
		}
	}

	public int getCardinalityMin() {
		return cardinalityMin;
	}

	public int getCardinalityMax() {
		return cardinalityMax;
	}

	public int getScope() {
		return scope;
	}

	public Set<String> getPairs() {
		return pairs;
	}

	public String getConstraint() {
		return constraint;
	}

	public String getName() {
		return name;
	}

	public ParserRuleContext getParseRuleContext() {
		return instantiateParseCtx;
	}

	public ParserRuleContext getSlotParseRuleContext() {
		return slotParseCtx;
	}

}
