/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

public final class Rule {

  private final NonTerminal lhs;
  private final int index;
  private final TreePattern tree;
  private final String match;
  private final String cost;
  private final String action;

  Rule(NonTerminal lhs, int index, TreePattern tree, String match, String cost, String action) {
    this.lhs = lhs;
    this.index = index;
    this.tree = tree;
    this.match = match;
    this.cost = cost;
    this.action = action;
  }

  public NonTerminal lhs() {
    return lhs;
  }

  public int index() {
    return index;
  }

  public TreePattern tree() {
    return tree;
  }

  public String getMatch() {
    return match;
  }

  public String getCost() {
    return cost;
  }

  public String getAction() {
    return action;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(lhs);
    sb.append(": ");
    if (tree == null)
      sb.append("default");
    else
      sb.append(tree);
    return sb.toString();
  }

}

