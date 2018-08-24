/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

public final class Terminal extends Symbol {

  private final int arity;

  public Terminal(String name, int arity) {
    super(name);
    if (arity < 0)
      throw new IllegalArgumentException();
    this.arity = arity;
  }

  public int arity() {
    return arity;
  }

  public String op() {
    return name();
  }

  public String type() {
    String name = name();
    int index = name.lastIndexOf('.');
    return name.substring(0, index)+"."+name.substring(index+1).toLowerCase();
  }

}

