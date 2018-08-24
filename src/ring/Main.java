/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Main {

  private static final boolean INLINE_TREES = true;

  static final Grammar grammar = new Grammar();

  public static void main(String[] args) {
    PrintStream pout = System.out;
    int argi;
    for (argi = 0; argi < args.length; argi++) {
      String arg = args[argi];
      if (arg.equals("-o")) {
        if (argi+1 == args.length) {
          System.err.println("-o parameter missing");
          return;
        }
        try {
          pout = new PrintStream(new FileOutputStream(args[argi+1]));
        } catch (IOException e) {
          System.err.println("Unable to open output file`"+args[argi+1]+"'");
          return;
        }
        argi++;
      } else
        break;
    }
    if (argi+1 != args.length) {
      System.err.println("usage: ring [-o <output>] input");
      return;
    }
    Reader in;
    try {
      in = new InputStreamReader(new FileInputStream(args[argi]));
    } catch (IOException e) {
      System.err.println("Unable to open input file`"+args[argi]+"'");    
      return;
    }
    IndentWriter out = new IndentWriter(pout, "  ");
    try {
      Parser parser = new Parser(in);
      parser.CompilationUnit(out);
      out.flush();
    } catch (Throwable e) {
      System.err.println("ring: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void emitTreeMatcher(IndentWriter out) {

    grammar.validate();

    if (grammar.getNonTerminalCount() > 0) {

      Hashtable trees = new Hashtable();
      for (Enumeration i = grammar.getNonTerminals(); i.hasMoreElements(); ) {
        NonTerminal nt = (NonTerminal)i.nextElement();
        for (Enumeration j = nt.getRules(); j.hasMoreElements(); ) {
          Rule rule = (Rule)j.nextElement();
          if (rule.tree().root() instanceof Terminal) {
            Vector common = (Vector)trees.get(rule.tree());
            if (common == null) {
              common = new Vector();
              trees.put(rule.tree(), common);
            }
            common.addElement(rule);
          }
        }
        emitNonTerminal(out, nt);
      }

      out.println("private final TreeNode node$;");
      out.println("private "+grammar.currentClass()+" left$;");
      out.println("private "+grammar.currentClass()+" middle$;");
      out.println("private "+grammar.currentClass()+" right$;");
      out.println();

      out.println("public "+grammar.currentClass()+"(TreeNode node$) {");
      out.indent();

      out.println("this.node$ = node$;");

      out.println("switch (node$.op()) {");

      Enumeration e = grammar.getTerminals();

      while (e.hasMoreElements()) {
        Terminal terminal = (Terminal)e.nextElement();

        int tcount = 0;
        for (Enumeration i = trees.keys(); i.hasMoreElements(); ) {
          TreePattern tree = (TreePattern)i.nextElement();
          if (tree.root() == terminal)
            tcount++;
        }

        if (tcount > 0) {
          out.println("case "+terminal.op()+": {");
          out.indent();

          out.println("final "+terminal.type()+" $1 = ("+terminal.type()+")node$;");

          switch (terminal.arity()) {
          case 1:
            out.println("left$ = new "+grammar.currentClass()+"($1.left());");
            break;
          case 2:
            out.println("left$ = new "+grammar.currentClass()+"($1.left());");
            out.println("right$ = new "+grammar.currentClass()+"($1.right());");
            break;
          case 3:
            out.println("left$ = new "+grammar.currentClass()+"($1.left());");
            out.println("middle$ = new "+grammar.currentClass()+"($1.middle());");
            out.println("right$ = new "+grammar.currentClass()+"($1.right());");
            break;
          }


          int tindex = 0;
          for (Enumeration i = trees.keys(); i.hasMoreElements(); tindex++) {
            TreePattern tree = (TreePattern)i.nextElement();
            if (tree.root() == terminal)
              if (!INLINE_TREES)
                out.println("tree$"+tindex+"($1);");
              else
                emitTree(out, tree, (Vector)trees.get(tree), tindex);
          }

          out.println("break;");
          out.unindent();
          out.println("}");
        }

      }

      out.println("default:");
      out.indent();

      out.println("switch (node$.arity()) {");
      out.println("case 0:");
      out.indent();
      out.println("break;");
      out.unindent();
      out.println("case 1:");
      out.indent();
      out.println("left$ = new "+grammar.currentClass()+"(node$.left());");
      out.println("break;");
      out.unindent();
      out.println("case 2:");
      out.indent();
      out.println("left$ = new "+grammar.currentClass()+"(node$.left());");
      out.println("right$ = new "+grammar.currentClass()+"(node$.right());");
      out.println("break;");
      out.unindent();
      out.println("case 3:");
      out.indent();
      out.println("left$ = new "+grammar.currentClass()+"(node$.left());");
      out.println("middle$ = new "+grammar.currentClass()+"(node$.middle());");
      out.println("right$ = new "+grammar.currentClass()+"(node$.right());");
      out.println("break;");
      out.unindent();
      out.println("default:");
      out.indent();
      out.println("throw new Error(\"Illegal arity\");");
      out.unindent();
      out.println("}");

      out.unindent();
      out.println("}");
      out.println();

      for (Enumeration i = grammar.getNonTerminals(); i.hasMoreElements(); ) {
        NonTerminal nt = (NonTerminal)i.nextElement();

        Rule defaultRule = nt.getDefaultRule();
        if (defaultRule != null) {

          int ruleCount = nt.getRuleCount();
          String cost = defaultRule.getCost();
          int chainCount = nt.getChainCount();
          if (cost != null || chainCount > 0) {

            if (ruleCount > 0)
              out.print("if ("+nt+"$id == 0) ");
            if (cost != null) {
              out.println("{");
              out.indent();
              if (nt.variables != null) {
                out.println("final "+nt+" $$ = new "+nt+"();");
                cost = replace(cost, "@@", "$$");
              }
              out.println("final TreeNode $1 = node$;");
              cost = replace(cost, "@1", "$1");
              out.println(cost);
              if (nt.variables != null)
                out.println(nt+"  = $$;");
            }
            if (chainCount > 0)
              out.println(nt+"$closure();");
            if (cost != null) {
              out.unindent();
              out.println("}");
            }

          }
        }
      }

      out.unindent();
      out.println("}");
      out.println();

      for (Enumeration i = grammar.getNonTerminals(); i.hasMoreElements(); ) {
        NonTerminal nt = (NonTerminal)i.nextElement();

        int chainCount = nt.getChainCount();
        if (chainCount > 0) {
          out.println("private final void "+nt+"$closure() {");
          out.indent();

          out.println("final "+grammar.currentClass()+" $1 = this;");

          for (Enumeration j = nt.getChains(); j.hasMoreElements(); ) {
            Rule rule = (Rule)j.nextElement();

            String match = rule.getMatch();
            if (match != null) {
              out.print("if (");

              int index = 1;
              for (Enumeration k = rule.tree().symbols(); k.hasMoreElements(); index++) {
                Symbol symbol = (Symbol)k.nextElement();
                if (symbol instanceof Terminal)
                  match = replace(match, "@"+index, "$"+index);
                else
                  match = replace(match, "@"+index, "$"+index+"."+symbol);
              }

              out.print(match);
              out.print(") ");
            }
            out.println("{");
            out.indent();

            if (rule.lhs().variables != null)
              out.println("final "+rule.lhs()+" $$ = new "+rule.lhs()+"();");

            String cost = rule.getCost();
            if (cost != null) {

              cost = replace(cost, "@@", "$$");

              int index = 1;
              for (Enumeration k = rule.tree().symbols(); k.hasMoreElements(); index++) {
                Symbol symbol = (Symbol)k.nextElement();
                if (symbol instanceof Terminal)
                  cost = replace(cost, "@"+index, "$"+index);
                else
                  cost = replace(cost, "@"+index, "$"+index+"."+symbol);
              }

              out.print(cost);

            }

            out.print("if ("+rule.lhs()+"$id == 0");
            if (rule.lhs().getFunction() != null)
              out.print(" || "+rule.lhs()+".better$($$)");
            out.println(") {");
            out.indent();

            if (rule.lhs().variables != null)
              out.println(rule.lhs()+" = $$;");

            int ruleIndex = rule.index();
            int ruleCount = rule.lhs().getRuleCount();
            if (ruleIndex > 127 && ruleCount < 256)
              out.println(rule.lhs()+"$id = (byte)"+ruleIndex+";");
            else if (ruleIndex > 32767 && ruleCount < 65536)
              out.println(rule.lhs()+"$id = (short)"+ruleIndex+";");
            else
              out.println(rule.lhs()+"$id = "+ruleIndex+";");

            if (rule.lhs().getChainCount() > 0)
              out.println(rule.lhs()+"$closure();");

            out.unindent();
            out.println("}");

            out.unindent();
            out.println("}");

          }
          out.unindent();
          out.println("}");
          out.println();
        }
      }

      if (!INLINE_TREES) {
        int tindex = 0;
        for (Enumeration tenum = trees.keys(); tenum.hasMoreElements(); tindex++) {
          TreePattern tree = (TreePattern)tenum.nextElement();
          out.println("private final void tree$"+tindex+"(final "+((Terminal)tree.root()).type()+" $1) {");
          out.indent();
          emitTree(out, tree, (Vector)trees.get(tree), tindex);
          out.unindent();
          out.println("}");
          out.println();
        }
      }

    }
  }

  private static void emitTree(IndentWriter out, TreePattern tree, Vector rules, int tindex) {
    out.print("if (");
    emitMatch(out, tree, false, "this");
    out.println(") {");
    out.indent();
    declareParameters(out, tree, false, 1, "this");

    for (int i = 0; i < rules.size(); i++) {
      Rule rule = (Rule)rules.elementAt(i);
      String match = rule.getMatch();
      if (match != null) {
        out.print("if (");

        int index = 1;
        for (Enumeration j = tree.symbols(); j.hasMoreElements(); index++) {
          Symbol symbol = (Symbol)j.nextElement();
          if (symbol instanceof Terminal)
            match = replace(match, "@"+index, "$"+index);
          else
            match = replace(match, "@"+index, "$"+index+"."+symbol);
        }

        out.print(match);
        out.print(") ");
      }
      out.println("{");
      out.indent();

      if (rule.lhs().variables != null)
        out.println("final "+rule.lhs()+" $$ = new "+rule.lhs()+"();");

      String cost = rule.getCost();
      if (cost != null) {

        cost = replace(cost, "@@", "$$");

        int index = 1;
        for (Enumeration j = tree.symbols(); j.hasMoreElements(); index++) {
          Symbol symbol = (Symbol)j.nextElement();
          if (symbol instanceof Terminal)
            cost = replace(cost, "@"+index, "$"+index);
          else
            cost = replace(cost, "@"+index, "$"+index+"."+symbol);
        }

        out.print(cost);
      }

      out.print("if ("+rule.lhs()+"$id == 0");
      if (rule.lhs().getFunction() != null)
        out.print(" || "+rule.lhs()+".better$($$)");
      out.println(") {");
      out.indent();

      if (rule.lhs().variables != null)
        out.println(rule.lhs()+" = $$;");

      int ruleIndex = rule.index();
      int ruleCount = rule.lhs().getRuleCount();
      if (ruleIndex > 127 && ruleCount < 256)
        out.println(rule.lhs()+"$id = (byte)"+ruleIndex+";");
      else if (ruleIndex > 32767 && ruleCount < 65536)
        out.println(rule.lhs()+"$id = (short)"+ruleIndex+";");
      else
        out.println(rule.lhs()+"$id = "+ruleIndex+";");

      if (rule.lhs().getChainCount() > 0)
        out.println(rule.lhs()+"$closure();");

      out.unindent();
      out.println("}");

      out.unindent();
      out.println("}");
    }

    out.unindent();
    out.println("}");
  }

  private static void emitSymbol(IndentWriter out, TreePattern tree, String from) {
    if (tree.root() instanceof Terminal) {
      out.print(" && ");
      emitMatch(out, tree, true, from+".left$");
    } else {
      NonTerminal nt = (NonTerminal)tree.root();
      if (nt.getDefaultRule() == null)
        out.print(" && "+from+".left$."+nt+"$id != 0");
    }
  }

  private static void emitMatch(IndentWriter out, TreePattern tree, boolean matchroot, String from) {
    Terminal terminal = (Terminal)tree.root();
    if (matchroot)
      out.print(from+".node$.op() == "+terminal.op());
    else
      out.print("true");

    TreePattern left = tree.left();
    if (left != null)
      if (left.root() instanceof Terminal) {
        out.print(" && ");
        emitMatch(out, left, true, from+".left$");
      } else {
        NonTerminal nt = (NonTerminal)left.root();
        if (nt.getDefaultRule() == null)
          out.print(" && "+from+".left$."+nt+"$id != 0");
      }

    TreePattern middle = tree.middle();
    if (middle != null)
      if (middle.root() instanceof Terminal) {
        out.print(" && ");
        emitMatch(out, middle, true, from+".middle$");
      } else {
        NonTerminal nt = (NonTerminal)middle.root();
        if (nt.getDefaultRule() == null)
          out.print(" && "+from+".middle$."+nt+"$id != 0");
      }

    TreePattern right = tree.right();
    if (right != null)
      if (right.root() instanceof Terminal) {
        out.print(" && ");
        emitMatch(out, right, true, from+".right$");
      } else {
        NonTerminal nt = (NonTerminal)right.root();
        if (nt.getDefaultRule() == null)
          out.print(" && "+from+".right$."+nt+"$id != 0");
      }
  }

  private static int declareParameters(IndentWriter out, TreePattern tree, boolean declareRoot, int index, String from) {
    if (declareRoot) {
      Symbol symbol = tree.root();
      if (symbol instanceof NonTerminal)
        out.println("final "+grammar.currentClass()+" $"+index+" = "+from+";");
      else {
        Terminal terminal = (Terminal)tree.root();
        out.println("final "+terminal.type()+" $"+index+" = ("+terminal.type()+")"+from+".node$;");
      }
    }

    index++;

    TreePattern left = tree.left();
    if (left != null)
      index = declareParameters(out, left, true, index, from+".left$");

    TreePattern middle = tree.middle();
    if (middle != null)
      index = declareParameters(out, middle, true, index, from+".middle$");

    TreePattern right = tree.right();
    if (right != null)
      index = declareParameters(out, right, true, index, from+".right$");

    return index;
  }

  private static void emitNonTerminal(IndentWriter out, NonTerminal nt) {
    int ruleCount = nt.getRuleCount();
    if (ruleCount > 0)
      if (ruleCount < 256)
        out.println("private byte "+nt+"$id;");
      else if (ruleCount < 65536)
        out.println("private short "+nt+"$id;");
      else
        out.println("private int "+nt+"$id;");

    if (nt.variables != null) {
      out.println(nt.getModifiers()+nt+" "+nt+";");
      out.println();

      out.println(nt.getModifiers()+(grammar.currentIsStatic() ? "static " : " ")+"final class "+nt+" {");
      out.indent();
      out.println();

      for (int i = 0; i < nt.variables.size(); i++)
        out.println("public "+nt.variables.elementAt(i)+";");
      out.println();

      out.println(nt+"() { }");
      out.println();

      String function = nt.getFunction();
      if (function != null) {
        out.println("private boolean better$(final "+nt+" $$) {");
        out.indent();
        function = replace(function, "@@", "$$");
        out.println("return "+function+";");
        out.unindent();
        out.println("}");
        out.println();
      }

      out.unindent();
      out.println("}");
    }
    out.println();

    String ret = nt.getReturn();
    String signature = nt.getSignature();
    out.println(nt.getModifiers()+"final "+ret+" "+nt+signature+" {");
    out.indent();

    out.println("final "+grammar.currentClass()+" $$ = this;");

    if (ruleCount > 0) {
      if (ruleCount > 127 && ruleCount < 256)
        out.println("switch ("+nt+"$id & 0xFF) {");
      else if (ruleCount > 32767 && ruleCount < 65536)
        out.println("switch ("+nt+"$id & 0xFFFF) {");
      else
        out.println("switch ("+nt+"$id) {");
      out.println("case 0: {");
      out.indent();
    }

    Rule defaultRule = nt.getDefaultRule();
    if (defaultRule == null) {
      out.println("if (true)");
      out.indent();
      out.println("throw new Error(\"No match\");");
      out.unindent();
    } else {
      String action = defaultRule.getAction();
      if (action != null) {
        out.println("final TreeNode $1 = $$.node$;");
        out.print("if (true) ");
        action = replace(action, "@1", "$1");
        action = replace(action, "@@", "$$."+nt);
        out.println(action);
      }
    }

    if (ruleCount > 0) {
      out.println("break;");
      out.unindent();
      out.println("}");

      for (Enumeration i = nt.getRules(); i.hasMoreElements(); ) {
        Rule rule = (Rule)i.nextElement();
        TreePattern tree = rule.tree();
        String action = rule.getAction();
        if (action != null) {
          out.println("case "+rule.index()+": {");
          out.indent();

          declareParameters(out, tree, true, 1, "$$");
          out.print("if (true) ");

          action = replace(action, "@@", "$$."+nt);

          int index = 1;
          for (Enumeration j = tree.symbols(); j.hasMoreElements(); index++) {
            Symbol symbol = (Symbol)j.nextElement();
            if (symbol instanceof Terminal)
              action = replace(action, "@"+index, "$"+index);
            else
              action = replace(action, "@"+index, "$"+index+"."+symbol);
          }

          out.print(action);

          out.println("break;");
          out.unindent();
          out.println("}");
        }
      }

      out.println("default:");
      out.indent();
      out.println("throw new Error(\"Unimplemented rule\");");
      out.unindent();
      out.println("}");
    }

    if (ret.indexOf("void") == -1)
      out.println("throw new Error(\"Return expected\");");

    out.unindent();
    out.println("}");
    out.println();
  }

  private static String replace(String string, String a, String b) {
    StringBuffer sb = new StringBuffer();
    int start = 0;
    for (int end = string.indexOf(a, start); end != -1; end = string.indexOf(a, start)) {
      sb.append(string.substring(start, end));
      sb.append(b);
      start = end+a.length();
    }
    sb.append(string.substring(start));
    return sb.toString();
  }

}

