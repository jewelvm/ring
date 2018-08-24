/* RInG, Rewriting for Intermediate Grammar
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package ring;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class IndentWriter extends PrintWriter {

  public static final String DEFAULT_INDENT_CHARS = "\t";

  private final String indentChars;

  private int indentLevel = 0;
  private boolean lineStart = true;

  public IndentWriter(Writer out) {
    this(out, false, DEFAULT_INDENT_CHARS);
  }

  public IndentWriter(Writer out, String indentChars) {
    this(out, false, indentChars);
  }

  public IndentWriter(Writer out, boolean autoFlush) {
    this(out, autoFlush, DEFAULT_INDENT_CHARS);
  }

  public IndentWriter(Writer out, boolean autoFlush, String indentChars) {
    super(out, autoFlush);
    if (indentChars == null)
      throw new NullPointerException();
    this.indentChars = indentChars;
  }

  public IndentWriter(OutputStream out) {
    this(out, false, DEFAULT_INDENT_CHARS);
  }

  public IndentWriter(OutputStream out, String indentChars) {
    this(out, false, indentChars);
  }

  public IndentWriter(OutputStream out, boolean autoFlush) {
    this(out, autoFlush, DEFAULT_INDENT_CHARS);
  }

  public IndentWriter(OutputStream out, boolean autoFlush, String indentChars) {
    super(out, autoFlush);
    if (indentChars == null)
      throw new NullPointerException();
    this.indentChars = indentChars;
  }

  private void printIndentation() {
    if (lineStart) {
      lineStart = false;
      for (int i = 0; i < indentLevel; i++)
        super.print(indentChars);
    }
  }

  public int indentLevel() {
    return indentLevel;
  }

  public String indentChars() {
    return indentChars;
  }

  public void indent() {
    if (!lineStart)
      throw new IllegalStateException("Attempt to indent in the middle of a line");
    indentLevel++;
  }

  public void unindent() {
    if (!lineStart)
      throw new IllegalStateException("Attempt to unindent in the middle of a line");
    if (indentLevel == 0)
      throw new IllegalStateException("Indent level underflow");
    indentLevel--;
  }

  public void print(boolean value) {
    printIndentation();
    super.print(value);
  }

  public void print(char value) {
    printIndentation();
    super.print(value);
  }

  public void print(char[] value) {
    printIndentation();
    super.print(value);
  }

  public void print(float value) {
    printIndentation();
    super.print(value);
  }

  public void print(double value) {
    printIndentation();
    super.print(value);
  }

  public void print(int value) {
    printIndentation();
    super.print(value);
  }

  public void print(long value) {
    printIndentation();
    super.print(value);
  }

  public void print(Object value) {
    printIndentation();
    super.print(value);
  }

  public void print(String value) {
    printIndentation();
    super.print(value);
  }

  public void println() {
    super.println();
    lineStart = true;
  }

  public void println(boolean value) {
    print(value);
    println();
  }

  public void println(char value) {
    print(value);
    println();
  }

  public void println(char[] value) {
    print(value);
    println();
  }

  public void println(float value) {
    print(value);
    println();
  }

  public void println(double value) {
    print(value);
    println();
  }

  public void println(int value) {
    print(value);
    println();
  }

  public void println(long value) {
    print(value);
    println();
  }

  public void println(Object value) {
    print(value);
    println();
  }

  public void println(String value) {
    print(value);
    println();
  }

}

