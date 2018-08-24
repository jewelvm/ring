/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

public abstract class Symbol {

  private final String name;

  protected Symbol(String name) {
    if (name == null)
      throw new NullPointerException();
    this.name = name;
  }
  
  public final String name() {
    return name;
  }

  public abstract int arity();

  public String toString() {
    return name;
  }

}

