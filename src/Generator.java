/**
 *
 * Parse tree:
 * (grammarSpec
 *  (grammarDecl
 *   (grammarType grammar)
 *   (identifier expr) ;)
 *   (rules
 *    (ruleSpec
 *     (parserRuleSpec expr :
 * 		    (ruleBlock
 * 		     (ruleAltList
 * 		      (labeledAlt
 * 		       (alternative
 * 			      (element
 * 			        (atom (ruleref expr)))
 * 			      (element
 * 			        (atom (terminal '+')))
 * 			      (element (atom (ruleref expr)))))
 * 		      |
 * 		      (labeledAlt
 * 		       (alternative
 * 			(element
 * 			 (atom (terminal INT))))))) ;
 *
 * 		    exceptionGroup))
 *
 *    (ruleSpec
 *     (lexerRuleSpec INT :
 * 		   (lexerRuleBlock
 * 		    (lexerAltList
 * 		     (lexerAlt
 * 		      (lexerElements
 * 		       (lexerElement (lexerAtom [0-9])
 * 				     (ebnfSuffix +)))))) ;)))
 * 		   <EOF>)
 *
 */

import java.util.HashMap;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.Random;
import org.antlr.parser.antlr4.*;

public class Generator extends ANTLRv4ParserBaseVisitor<Void> implements
    ANTLRv4ParserVisitor<Void> {

  private ParseTree m_tree;
  private Random  m_random;
  private  HashMap<String, ANTLRv4Parser.ParserRuleSpecContext> m_rules = new HashMap();
  private int m_depth = 42;
  private String m_code;

  public Generator(ParseTree tree, HashMap rules ) {
    m_tree = tree;
    m_random = new Random();
    m_rules = rules;
    m_code = "";
  }

  @Override
  public Void visitRuleref(ANTLRv4Parser.RulerefContext ctx) {
    ANTLRv4Parser.ParserRuleSpecContext pctx = m_rules.get(ctx.RULE_REF().toString());
    visitChildren(pctx);
    return null;
  }

  @Override
  public Void visitRuleAltList(ANTLRv4Parser.RuleAltListContext ctx) {
    int alternative = m_random.nextInt(ctx.labeledAlt().size());
    if (m_depth > 0) {
      m_depth--;
      visitChildren(ctx.labeledAlt(alternative));
      m_depth++;
    }

    return null;
  }

  @Override
  public Void visitAtom(ANTLRv4Parser.AtomContext ctx) {
    if ( ctx.terminal() != null) {
      if ( ctx.terminal().TOKEN_REF() != null ) {
        if ( ctx.terminal().TOKEN_REF().toString().equals("INT")) {
          m_code += m_random.nextInt(100);
        }
      }
      else if ( ctx.terminal().STRING_LITERAL() != null ) {
        m_code += "+";
      }
    } else if ( ctx.ruleref() != null ) {
        visit(ctx.ruleref());
      }

      return null;
  }

  public String printContext(ParserRuleContext ctx) {
    int a = ctx.start.getStartIndex();
    int b = ctx.stop.getStopIndex();

    Interval interval = new Interval(a, b);
    return ctx + ":" + ctx.start.getInputStream().getText(interval);
  }

  public String getCode() { return m_code;}
}

