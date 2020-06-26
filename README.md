# Homework 1 - Compilers course Spring 2020

## Part 1 - LL(1) Calculator Parser
Implementation of a simple calculator. The calculator accepts expressions with the addition, subtraction, multiplication and division operators, as well as parentheses.
The grammar (for multi-digit numbers) after transforming it to LL(1) grammar:

    1. exp → term restExp
    2. restExp → ε
    3.               | + term restExp
    4.               | - term restExp
    5. term → par restTerm
    6. restTerm → ε
    7.                 | * par restTerm
    8.                 | / par restTerm
    9. par → (exp)
    10.         | num
    11. num → digit restNum
    12. restNum → ε
    13.                 | digit restNum
    14. digit → 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

### Compile
```
make compile
```
### Execute
```
make execute
```
__Notes:__ Give input from terminal or file. Space token is not recognised, so 2 + 4 * 3 will not be accepted, but 2+4*3 will be.

### Clean
```
make clean
```

## Part 2 - Translator to Java
Implementation of a parser and translator for a language supporting string operations. The language supports concatenation (+) and "reverse" operators over strings, function definitions and calls, conditionals (if-else i.e, every "if" must be followed by an "else"), and the following logical expression:

* is-prefix-of (string1 prefix string2): Whether string1 is a prefix of string2.
All values in the language are strings.

The precedence of the operator expressions is defined as: _precedence(if)_ < _precedence(concat)_ < _precedence(reverse)_.

The parser is based on the following context-free grammar:

    1. program --> declarations
    2. declarations --> fun_decl declarations
    3.			|fun_calls
    4. fun_calls --> call fun_calls
    5.			| ε
    6. fun_decl --> Identifier '(' id_list body '}'
    7. id_list --> Identifier restIds ')' '{'
    8.			|')' '{'
    9. restIds --> , Identifier restIds
    10.			| ε
    11. call --> Identifier '(' expr_list ')'
    12. expr_list --> exp restExr 
    13.			| ε
    14. restExpr --> , exp restExpr
    15.			| ε
    16. body --> exp
    17.		| ε
    18. exp --> call
    19.		|REVERSE exp
    20.		|exp CONCAT exp
    21.		|IF '(' prefix_exp ')' exp ELSE exp
    22.		| Identifier
    23.		| String_Literal
    24. prefix_exp --> exp PREFIX exp


The parser will translate the input language into Java.

### Implementation Tools
__JavaCUP__ is used for the generation of the parser combined with generated-one using __JFlex__.The output language is a subset of Java so it can be compiled using __javac__ and executed using __java__.

Type checking for the argument types or a check for the number of function arguments is not performed. It assumed that the program input will always be semantically correct like the examples below.

### Examples
__Input:__

    name()  {
        "John"
    }

    surname() {
        "Doe"
    }

    fullname(first_name, sep, last_name) {
        first_name + sep + last_name
    }

    name()
    surname()
    fullname(name(), " ", surname())

__Output(Java):__

    public class Main {
      public static void main(String[] args) {
          System.out.println(name());
          System.out.println(surname());
          System.out.println(fullname(name(), " ", surname()));
      }

      public static String name() {
          return "John";
      }

      public static String surname() {
          return "Doe";
      }

      public static String fullname(String first_name, String sep, String last_name) {
          return first_name + sep + last_name;
      }
    }

# Homework 2 - Compilers course Spring 2020
## MiniJava Static Checking (Semantic Analysis)

This homework introduces the semester project, which consists of building a compiler for MiniJava, a subset of Java. MiniJava is designed so that its programs can be compiled by a full Java compiler like javac.

Here is a partial, textual description of the language. Much of it can be safely ignored (most things are well defined in the grammar or derived from the requirement that each MiniJava program is also a Java program):

* MiniJava is fully object-oriented, like Java. It does not allow global functions, only classes, fields and methods. The basic types are int, boolean, int [] which is an array of int, and boolean [] which is an array of boolean. You can build classes that contain fields of these basic types or of other classes. Classes contain methods with arguments of basic or class types, etc.
* MiniJava supports single inheritance but not interfaces. It does not support function overloading, which means that each method name must be unique. In addition, all methods are inherently polymorphic (i.e., “virtual” in C++ terminology). This means that foo can be defined in a subclass if it has the same return type and argument types (ordered) as in the parent, but it is an error if it exists with other argument types or return type in the parent. Also all methods must have a return type--there are no void methods. Fields in the base and derived class are allowed to have the same names, and are essentially different fields.
* All MiniJava methods are “public” and all fields “protected”. A class method cannot access fields of another class, with the exception of its superclasses. Methods are visible, however. A class's own methods can be called via “this”. E.g., this.foo(5) calls the object's own foo method, a.foo(5) calls the foo method of object a. Local variables are defined only at the beginning of a method. A name cannot be repeated in local variables (of the same method) and cannot be repeated in fields (of the same class). A local variable x shadows a field x of the surrounding class.
* In MiniJava, constructors and destructors are not defined. The new operator calls a default void constructor. In addition, there are no inner classes and there are no static methods or fields. By exception, the pseudo-static method “main” is handled specially in the grammar. A MiniJava program is a file that begins with a special class that contains the main method and specific arguments that are not used. The special class has no fields. After it, other classes are defined that can have fields and methods.

__Notably__, an A class can contain a field of type B, where B is defined later in the file. But when we have "class B extends A”, A must be defined before B. As you'll notice in the grammar, MiniJava offers very simple ways to construct expressions and only allows < comparisons. There are no lists of operations, e.g., 1 + 2 + 3, but a method call on one object may be used as an argument for another method call. In terms of logical operators, MiniJava allows the logical and ("&&") and the logical not ("!"). For int and boolean arrays, the assignment and [] operators are allowed, as well as the a.length expression, which returns the size of array a. We have “while” and “if” code blocks. The latter are always followed by an “else”. Finally, the assignment "A a = new B();" when B extends A is correct, and the same applies when a method expects a parameter of type A and a B instance is given instead.

The MiniJava grammar in BNF can be downloaded [here](http://cgi.di.uoa.gr/~thp06/project_files/minijava-new-2020/minijava.html).
### Tools
The MiniJava grammar in __JavaCC__ form (minijava.jj) is included in this repository but it can also be downloaded [here](http://cgi.di.uoa.gr/~thp06/project_files/minijava-new-2020/minijava.jj). __JTB__ tool is used to convert it into a grammar that produces class hierarchies. The jar files needed for the Makefile are all included in the repository. __Note:__ Using differrent versions of the jars provided may not be compatible.

### Task description
The task is to write two visitors who will take control over the MiniJava input file and will tell whether it is semantically correct, or will print an error message. Compilation ends at the first error. The visitors are subclasses of the visitors generated by __JTB__ (GJDepthFirst Visitor is used here). The Main class runs the semantic analysis initiating the parser that was produced by __JavaCC__ and executing the visitors.

Also, for every MiniJava file, the program computes and prints some useful data for every class such as the names and the offsets of every field and method this class contains. For MiniJava we have only three types of fields (int, boolean and pointers). Ints are stored in 4 bytes, booleans in 1 byte and pointers in 8 bytes (we consider functions and arrays as pointers). Corresponding offsets are shown in the example below:

#### Input:

    class A{
      int i;
      boolean flag;
      int j;
      public int foo() {}
      public boolean fa() {}
    }

    class B extends A{
        A type;
        int k;
        public int foo() {}
        public boolean bla() {}
    }

#### Output:

    A.i : 0
    A.flag : 4
    A.j : 5
    A.foo : 0
    A.fa: 8
    B.type : 9
    B.k : 17
    B.bla : 16
    
### Compile
```
make compile
```
### Execute
```
java Main [file1] [file2] ... [fileN]
```

### Clean
```
make clean
```
