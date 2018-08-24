# RInG, Rewriting for Intermediate Grammar
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

JC=javac
JCC=javacc
JAR=jar cvfm

JARFILE=../lib/ring.jar

PACKAGES=ring

SOURCES=$(wildcard $(addsuffix /*.java,$(PACKAGES)))
CLASSES=$(addsuffix /*.class,$(PACKAGES))
GRAMMARS=$(wildcard $(addsuffix /*.jj,$(PACKAGES)))

MANIFEST=META-INF/MANIFEST.MF

.SUFFIXES: .jj .java .class
.PHONY: all clean

all: $(JARFILE)

clean:
	rm -f $(CLASSES) $(JARFILE)
	rm -f ring/ASCII_UCodeESC_CharStream.java ring/ParseException.java ring/Parser.java
	rm -f ring/ParserConstants.java ring/ParserTokenManager.java ring/Token.java
	rm -f ring/TokenMgrError.java ring/JavaCharStream.java

$(JARFILE): $(subst .jj,.java,$(GRAMMARS)) $(MANIFEST) $(subst .java,.class,$(SOURCES))
	$(JAR) $(JARFILE) $(MANIFEST) $(CLASSES)

.jj.java:
	$(JCC) $<

.java.class:
	$(JC) $<
