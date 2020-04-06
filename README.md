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
    6. fun_decl --> Identifier '(' id_list ')' '{' body '}'
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

### Compile
```
make compile
```
### Execute
```
make execute
```
### Clean
```
make clean
```
