package my_visitors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import types.*;
import visitor.GJDepthFirst;
import syntaxtree.*;

public class LLVMIRVisitor extends GJDepthFirst<String, String>{
    
    public SymbolTable sTable;
    public VTable vTables;
    String fileName;
    File LLVMfile;
    public String currentClass;
    public String currentMethod;

    public LLVMIRVisitor(SymbolTable stable, VTable vtables, File file) {
        this.sTable = stable;
        this.vTables = vtables;
        this.LLVMfile = file;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String visit(MainClass n, String argu) throws Exception {
        String _ret=null;
        if(!emit("define i32 @main() {\n\n")){
            throw new Exception("Something went wrong while compiling main function.");
        }
        String className;
        n.f0.accept(this, argu);
        className = n.f1.accept(this, argu);
        this.currentClass = className;
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        this.currentMethod = "main";
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        if(!emit("}")){
            throw new Exception("Something went wrong while compiling main function.");
        }
        return _ret;
    }

    public boolean emit(String str){

        FileWriter fw;
        try {
            fw = new FileWriter(this.LLVMfile, true);
            
        }catch (IOException ex) {
            return false;    
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.print(str);
        pw.close();
        return true;
    }

}