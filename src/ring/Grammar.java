/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Grammar {

  private final Hashtable terminals = new Hashtable();
  private final Vector scopes = new Vector();

  public Grammar() { }

  private Scope currentScope() {
    return (Scope)scopes.elementAt(scopes.size()-1);
  }

  public void enterScope(boolean isStatic, String name) {
    scopes.addElement(new Scope(isStatic, name));
  }

  public void exitScope() {
    scopes.removeElementAt(scopes.size()-1);
  }

  public String currentClass() {
    return currentScope().getName();
  }

  public boolean currentIsStatic() {
    return currentScope().isStatic();
  }

  public Rule declareRule(NonTerminal lhs, TreePattern pattern, String match, String cost, String action) {
    int index = pattern == null ? 0 : lhs.rules.size()+1;
    Rule rule = new Rule(lhs, index, pattern, match, cost, action);
    if (pattern == null) {
      if (lhs.defaultRule != null)
        throw new IllegalArgumentException("Default rule already declared");
      lhs.defaultRule = rule;
    } else {
      lhs.rules.addElement(rule);
      if (rule.tree().root() instanceof NonTerminal) {
        NonTerminal nt = (NonTerminal)rule.tree().root();
        nt.chains.addElement(rule);
      }
    }
    return rule;
  }

  public Terminal getTerminal(String op, int arity) {
    Terminal terminal = (Terminal)terminals.get(op);
    if (terminal == null) {
      terminal = new Terminal(op, arity);
      terminals.put(op, terminal);
    }
    if (terminal.arity() != arity)
      throw new IllegalArgumentException("Illegal terminal arity: "+terminal);
    return terminal;
  }

  public Enumeration getTerminals() {
    return terminals.elements();
  }

  public NonTerminal declareNonTerminal(String name, String modifiers, String ret, String signature, Vector variables, String function) {
    NonTerminal nonTerminal = getNonTerminal(name);
    if (nonTerminal.declared)
      throw new IllegalArgumentException("Non terminal redeclaration: "+name);
    nonTerminal.declared = true;
    nonTerminal.setFields(modifiers, ret, signature, variables, function);
    return nonTerminal;
  }

  public int getNonTerminalCount() {
    return currentScope().nonTerminals.size();
  }

  public NonTerminal getNonTerminal(String name) {
    NonTerminal nonTerminal = (NonTerminal)currentScope().nonTerminals.get(name);
    if (nonTerminal == null) {
      nonTerminal = new NonTerminal(name);
      currentScope().nonTerminals.put(name, nonTerminal);
    }
    return nonTerminal;
  }

  public Enumeration getNonTerminals() {
    return currentScope().nonTerminals.elements();
  }

  public void validate() {

    for (Enumeration i = getNonTerminals(); i.hasMoreElements(); ) {
      NonTerminal nt = (NonTerminal)i.nextElement();
      if (!nt.declared)
        throw new IllegalArgumentException("Undeclared non terminal: "+nt);
    }

  }

}

final class Scope {

  private final boolean isStatic;
  private final String name;
  final Hashtable nonTerminals = new Hashtable();

  Scope(boolean isStatic, String name) {
    this.isStatic = isStatic;
// anonymous classes have null names
//      if (name == null)
//        throw new NullPointerException();
    this.name = name;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public String getName() {
    return name;
  }

}

