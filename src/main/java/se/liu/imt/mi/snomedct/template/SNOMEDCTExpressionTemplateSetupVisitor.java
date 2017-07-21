package se.liu.imt.mi.snomedct.template;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import se.liu.imt.mi.snomedct.template.Slot;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateBaseVisitor;
import se.liu.imt.mi.snomedct.template.SNOMEDCTExpressionTemplateParser.SlotContext;
import se.liu.imt.mi.snomedct.template.Slot2Lexer;
import se.liu.imt.mi.snomedct.template.Slot2Parser;

public class SNOMEDCTExpressionTemplateSetupVisitor extends SNOMEDCTExpressionTemplateBaseVisitor<Slot> {
	
	private MultiValuedMap<ParserRuleContext, Slot> slotMap;
	
	

	public MultiValuedMap<ParserRuleContext, Slot> getSlotMap() {
		return slotMap;
	}



	public SNOMEDCTExpressionTemplateSetupVisitor() {
		super();
		
		slotMap = new HashSetValuedHashMap<ParserRuleContext, Slot>();
	}



	@Override
	public Slot visitSlot(SlotContext ctx) {

		CodePointCharStream cs = CharStreams.fromString(ctx.getText());
		Slot2Lexer lexer = new Slot2Lexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Slot2Parser parser = new Slot2Parser(tokens);
		ParseTree tree = null;
		try {
			tree = parser.innerSlot();
		} catch (ParseCancellationException pce) {
			throw pce;
		}

		Slot slot = null;

		if (tree.getClass() == se.liu.imt.mi.snomedct.template.Slot2Parser.InnerSlotContext.class)
			// should always happen, PCE should be thrown otherwise
			slot = new Slot((se.liu.imt.mi.snomedct.template.Slot2Parser.InnerSlotContext) tree, ctx);

		slotMap.put(slot.getParseRuleContext(), slot);

		return slot;
	}

}
