/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

import java.util.Enumeration;
import java.util.Vector;

public final class TreePattern {

  private final Symbol root;
  private final TreePattern[] sons;

  public TreePattern(Symbol root, TreePattern[] sons) {
    if (root.arity() != sons.length)
      throw new IllegalArgumentException("Illegal terminal arity: "+root);
    this.root = root;
    this.sons = (TreePattern[])sons.clone();
  }

  public Symbol root() {
    return root;
  }

  public TreePattern left() {
    return sons.length < 1 ? null : sons[0];
  }

  public TreePattern middle() {
    return sons.length < 3 ? null : sons[1];
  }

  public TreePattern right() {
    return sons.length < 2 ? null : sons[sons.length-1];
  }

  public Enumeration sons() {
    Vector vector = new Vector();
    for (int i = 0; i < sons.length; i++)
      vector.addElement(sons[i]);
    return vector.elements();
  }

  public Enumeration symbols() {
    Vector vector = new Vector();
    vector.addElement(root);
    for (int i = 0; i < sons.length; i++)
      for (Enumeration j = sons[i].symbols(); j.hasMoreElements(); ) {
        Symbol symbol = (Symbol)j.nextElement();
        vector.addElement(symbol);
      }
    return vector.elements();
  }

  public int hashCode() {
    int hashCode = root.hashCode();
    for (int i = 0; i < sons.length; i++)
      hashCode += i*sons[i].hashCode();
    return hashCode;
  }

  public boolean equals(Object object) {
    if (object instanceof TreePattern) {
      TreePattern tree = (TreePattern)object;
      if (!root.equals(tree.root))
        return false;
      if (sons.length != tree.sons.length)
        return false;
      for (int i = 0; i < sons.length; i++)
        if (!sons[i].equals(tree.sons[i]))
          return false;
      return true;
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(root);
    if (sons.length > 0) {
      sb.append('(');
      for (int i = 0; i < sons.length; i++) {
        sb.append(sons[i]);
        if (i < sons.length-1)
          sb.append(',');
      }
      sb.append(')');
    }
    return sb.toString();
  }

}

