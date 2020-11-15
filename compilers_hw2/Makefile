all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Main.java

clean:
	rm -f visitor/*.class syntaxtree/*.class types/*.class my_visitors/*.class *.class *~