JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class: ; $(JC) $(JFLAGS) $*.java

CLASSES = \
    p2pws.java \
    p2padmin.java \
    hashfunction.java \
    add.java

default: classes

classes: $(CLASSES:.java=.class)

clean: ; $(RM) *.class
