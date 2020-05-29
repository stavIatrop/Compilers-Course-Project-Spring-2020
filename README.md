# Homework 3 - Compilers course Spring 2020
## Generating intermediate code (MiniJava -> LLVM). 

This homework is the sequence of hw2 and converts MiniJava code into the intermediate representation used by the LLVM compiler project.

## Types

Some of the available types that might be useful are:

    i1 - a single bit, used for booleans (practically takes up one byte)
    i8 - a single byte
    i8* - similar to a char* pointer
    i32 - a single integer
    i32* - a pointer to an integer, can be used to point to an integer array
    static arrays, e.g., [20 x i8] - a constant array of 20 characters

## V-Table

If you do not remember or haven't seen how a virtual table (v-table) is constructed, essentially it is a table of function pointers, pointed at by the first 8 bytes of an object. The v-table defines an address for each dynamic function the object supports. Consider a function `foo` in position 0 and `bar` in position 1 of the table (with actual offset 8). If a method is overridden, the overriding version is inserted in the same location of the virtual table as the overridden version. Virtual calls are implemented by finding the address of the function to call through the virtual table. If we wanted to depict this in C, imagine that object `obj` is located at location `x` and we are calling `foo` which is in the 3rd position (offset 16) of the v-table. The address of the function that is going to be called is in memory location `(*x) + 16`.

## Execution

You will need to execute the produced LLVM IR files in order to see that their output is the same as compiling the input java file with `javac` and executing it with `java`. To do that, you will need `Clang with version >=4.0.0`. You may download it on your Linux machine, or use it via SSH on the linuxvm machines.

### In Ubuntu Trusty

* `sudo apt update && sudo apt install clang-4.0`
* Save the code to a file (e.g. ex.ll)
* `clang-4.0 -o out1 ex.ll`
* `./out1`

### In linuxvm machines

* `/home/users/thp06/clang/clang -o out1 ex.ll`
* `./out1`

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
