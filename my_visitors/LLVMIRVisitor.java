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
    public boolean classVar;
    public Integer register;
    public boolean primaryExp;

    public LLVMIRVisitor(SymbolTable stable, VTable vtables, File file) {
        this.sTable = stable;
        this.vTables = vtables;
        this.LLVMfile = file;
        this.register = 0;
        this.primaryExp = false;
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
        this.classVar = false;
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        if(!emit("\tret i32 0\n}\n\n")){
            throw new Exception("Something went wrong while compiling main function.");
        }
        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        this.currentClass = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        this.classVar = true;
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return _ret;
    }
    
    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        this.currentClass = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        this.classVar = true;
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        return _ret;
    }


    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String retType;
        retType = n.f1.accept(this, argu);
        if (retType == "int") {
            retType = "i32";
        }else if (retType == "boolean") {
            retType = "i1";
        }else if (retType == "int[]") {
            retType = "i32*" ;
        }else {
            retType = "i8*";
        }
        this.currentMethod = n.f2.accept(this, argu);
        FunInfo fInfo = sTable.lookupMethod(this.currentClass, this.currentMethod, null);
        String emitStr = "define " + retType + " @" + this.currentClass + "." + this.currentMethod + "(i8* %this";
        if (fInfo.arg_types.isEmpty()) {
            emitStr = emitStr + ") {\n";
        }else {
            for (String arg : fInfo.arg_types.keySet()) {

                String argType = fInfo.arg_types.get(arg);
                if (argType == "int") {
                    emitStr = emitStr + ", " + "i32 " + "%." + arg;
                }else if (argType == "boolean") {
                    emitStr = emitStr + ", " + "i1 " + "%." + arg;
                }else if (argType == "int[]") {
                    emitStr = emitStr + ", " + "i32* " + "%." + arg;
                }else {
                    emitStr = emitStr + ", " + "i8* " + "%." + arg;
                }
            }
            emitStr = emitStr + ") {\n";
        }
        if (!emit(emitStr)) {
            throw new Exception("Something went wrong while compiling method declaration.");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        this.classVar = false;
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        emitStr = "}\n\n";
        emit(emitStr);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, String argu) throws Exception {
        String _ret=null;
        String type, id;
        type = n.f0.accept(this, argu);
        id = n.f1.accept(this, argu);
        if (!this.classVar) {       //if it is a local variable, allocate memory on the stack
            if (type == "int") {
                type = "i32";
            }else if (type == "boolean"){
                type = "i1";
            }else if (type == "int[]"){
                type = "i32*";
            }else {
                type = "i8*";
            }
            String emitStr;
            emitStr = "\t%" + id + " = alloca " + type + "\n\n";
            if (!emit(emitStr)){
                throw new Exception("Something went wrong while declaring " + id + ".");
            }
        }
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, String argu) throws Exception {
        String _ret=null;
        String id;
        id = n.f0.accept(this, argu);
        String[] ret = sTable.lookupNameScope(this.currentClass, this.currentMethod, id);
        if (ret == null) {
            throw new Exception("Name " + id + " is not declared in assignment.");
        }
        String type = ret[0];
        if( type == "int") {
            type = "i32";
        }else if (type == "boolean") {
            type = "i1";
        }else if (type == "int[]"){
            type = "i32*";
        }else {
            type = "i8*";
        }

        String scope = ret[1];
        
        String emitStr = "\tstore " + type + " ";
        String var = "";
        if (scope == "class") {

            Integer offset = vTables.findOffset(this.currentClass, id);
            //TO BE CONTINUED
        }else if (scope == "fun_var") {
            var = "%" + id;
        } else {
            //TO BE CONTINUED
        }


        n.f1.accept(this, argu);
        String exp;
        exp = n.f2.accept(this, argu);
        emitStr = emitStr + exp + ", " + type + "* " + var + "\n\n";
        if (!emit(emitStr)) {
            throw new Exception("Something went wrong while compiling assignment statement of " + id + " variable");
        }
        n.f3.accept(this, argu);
        return _ret;
    }


    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String exp;
        exp = n.f2.accept(this, argu);
        String emitStr = "\tcall void (i32) @print_int(i32 " + exp + ")\n\n";
        if (!emit(emitStr)) {
            throw new Exception("Something went wrong while compiling print statement");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }


    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    public String visit(PrimaryExpression n, String argu) throws Exception {
        this.primaryExp = true;
        String primExp = n.f0.accept(this, argu);
        this.primaryExp = false;
        return primExp;
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, String argu) throws Exception {

        n.f0.accept(this, argu);
        this.primaryExp = false;
        String className;
        className = n.f1.accept(this, argu);
        String regCalloc = generateRegister();
        Integer objSize = vTables.getSizeOfObj(className, sTable);
        String emitString = "\t" + regCalloc + " = call i8* @calloc(i32 1, i32 " + objSize + ")\n\n";
        if (!emit(emitString)) {
            throw new Exception("Something went wrong with allocation expression.");
        }
        String regBitcast = generateRegister();
        emitString = "\t" + regBitcast + " = bitcast i8* " + regCalloc + " to i8***\n\n";
        if (!emit(emitString)) {
            throw new Exception("Something went wrong with allocation expression.");
        }
        String regGetEl = generateRegister();
        VTableInfo vInfo = vTables.VTablesHMap.get(className);
        Integer numMethods = vInfo.methodsVTable.size();
        emitString = "\t" + regGetEl + " = getelementptr [" + numMethods.toString() + " x i8*], [" +
                        numMethods.toString() + " x i8*]* @." + className + "_vtable, i32 0, i32 0\n\n";
        if (!emit(emitString)) {
            throw new Exception("Something went wrong with allocation expression.");
        }
        emitString = "\tstore i8** " + regGetEl + ", i8*** " + regBitcast + "\n\n";
        if (!emit(emitString)) {
            throw new Exception("Something went wrong with allocation expression.");
        }
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return regCalloc;
    }
    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, String argu) throws Exception {
        String id = n.f0.accept(this, argu);
        if (this.primaryExp) {

            String[] ret = sTable.lookupNameScope(this.currentClass, this.currentMethod, id);
            if (ret == null) {
                throw new Exception("Name " + id + " is not declared.");
            }

            String type = ret[0];
            
            String scope = ret[1];

            if (scope == "class") {

                Integer offset = vTables.findOffset(this.currentClass, id);
                //TO BE CONTINUED

            }else if (scope == "fun_var") {     //load the fun var
                if( type == "int") {

                    type = "i32";
                    String var = "%" + id;
                    String reg = generateRegister();
                    String emitStr = "\t" + reg + " = load " + type + ", " + type + "* " + var + "\n\n";
                    emit(emitStr);
                    return reg;
                }else if (type == "boolean") {
                    type = "i1";

                }else if (type == "int[]"){
                    type = "i32*";
                }else {
                    type = "i8*";
                } 
                
            } else {
                //TO BE CONTINUED
            }
        }
        

        return id;
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    public String visit(NodeToken n, String argu) {   
        return n.toString(); 
    }

    /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(BooleanArrayType n, String argu) {
        return "boolean[]";
    } 

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(IntegerArrayType n, String argu) throws Exception{
        return "int[]";
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

    public String generateRegister(){
        String reg = "%_" + this.register.toString();
        this.register += 1;
        return reg;
    }

    public String generateLabel(){
        return null;
    }
}