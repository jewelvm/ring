/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

import java.util.Enumeration;
import java.util.Vector;

public final class NonTerminal extends Symbol {

  boolean declared;

  private String modifiers;
  private String ret;
  private String signature;
  Vector variables;
  private String function;

  /*private*/ final Vector rules = new Vector();
  /*private*/ Rule defaultRule;
  /*private*/ final Vector chains = new Vector();

  NonTerminal(String name) {
    super(name);
  }

  public int arity() {
    return 0;
  }

  void setFields(String modifiers, String ret, String signature, Vector variables, String function) {
    this.modifiers = modifiers;
    this.ret = ret;
    this.signature = signature;
    this.variables = variables;
    this.function = function;
  }

  public String getModifiers() {
    return modifiers;
  }

  public String getReturn() {
    return ret;
  }

  public int getRuleCount() {
    return rules.size();
  }

  public Enumeration getRules() {
    return rules.elements();
  }

  public Rule getDefaultRule() {
    return defaultRule;
  }

  public String getSignature() {
    return signature;
  }

  public String getFunction() {
    return function;
  }

  public int getChainCount() {
    return chains.size();
  }

  public Enumeration getChains() {
    return chains.elements();
  }

}

