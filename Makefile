JAVAC=/usr/bin/javac
SRCDIR=src
BINDIR=bin

.SUFFIXES: .java .class

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= \
		Graph.class \
		SimulatorTwo.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: compile

compile: $(CLASSES:%.class=$(BINDIR)/%.class)

clean:
	rm $(BINDIR)/*.class

run:	$(CLASS_FILES)
	java -cp bin SimulatorTwo

javadoc:
	javadoc -d doc -cp bin -sourcepath src $(SRCDIR)/*.java

.PHONY: git-log

git-log:
	@git --no-pager log --pretty=format:%s | (ln=0; while read l; do echo $$ln\: $$l; ln=$$((ln+1)); done) | (head -n 10; echo ...; tail -n 10)
