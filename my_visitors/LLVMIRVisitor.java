package my_visitors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    public String messageSendClass;
    ArrayList<String> methodParams = new ArrayList<String>();

    public LLVMIRVisitor(SymbolTable stable, VTable vtables, File file) {
        this.sTable = stable;
        this.vTables = vtables;
        this.LLVMfile = file;
        this.register = 0;
        this.primaryExp = false;
        this.messageSendClass = "";
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
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, String argu) throws Exception {
        String _ret=null;
        String regPrimExp;
        regPrimExp = n.f0.accept(this, argu);
        String emitString;
        String regBitcast = generateRegister();
        emitString = "\t" + regBitcast + " = bitcast i8* " + regPrimExp + " to i8***\n\n";
        emit(emitString);
        String regLoad = generateRegister();
        emitString = "\t" + regLoad + " = load i8**, i8*** " + regBitcast + "\n\n";
        emit(emitString);
        
        n.f1.accept(this, argu);
        String methodName;
        methodName = n.f2.accept(this, argu);
        String regGetElem = generateRegister();

        Integer methodOffset = vTables.findMethodOffset(this.messageSendClass, methodName);
        emitString = "\t" + regGetElem + " = getelementptr i8*, i8** " + regLoad + ", i32 " + methodOffset + "\n\n";
        emit(emitString);
        String regLoad2 = generateRegister();
        emitString = "\t" + regLoad2 + " = load i8*, i8** " + regGetElem + "\n\n";
        emit(emitString);
        
        String regBitcast2 = generateRegister();
        
        FunInfo fInfo = sTable.lookupMethod(this.messageSendClass, methodName, null);
        String retType = fInfo.return_type;
        if (retType == "int")
            retType = "i32";
        else if (retType == "boolean")
            retType = "i1";
        else if (retType == "int[]")
            retType = "i32*";
        else
            retType = "i8*";
        emitString = "\t" + regBitcast2 + " = bitcast i8* " + regLoad2 + " to " + retType + " (i8*";

        ArrayList<String> arg_types = new ArrayList<String>(fInfo.arg_types.values());

        for ( String arg : arg_types) {
            if (arg == "int")
                arg = "i32";
            else if (arg == "boolean")
                arg = "i1";
            else if (arg == "int[]")
                arg = "i32*";
            else
                arg = "i8*";
            
            emitString = emitString + ", " + arg;
        }
        emitString = emitString + ")*\n\n";
        emit(emitString);

        
        n.f3.accept(this, argu);
        String expStr;
        expStr = n.f4.accept(this, argu);
        String regCall = generateRegister();
        emitString = "\t" + regCall + " = call " + retType + " " + regBitcast2 + "(i8* " + regPrimExp;

        if (expStr != null) {

            ArrayList<String> expList = new ArrayList<String>();
            int j = 0;
            if (expStr.contains(",")) {
                int i;
                for (i = 0; i < expStr.length(); i++) {
                    if (expStr.charAt(i) == ',') {
                        String sub;
                        sub = expStr.substring(j, i);
                        j = i + 1;
                        expList.add(sub);
                    }
                }
                String sub;
                sub = expStr.substring(j, i);
                expList.add(sub);
            }else {
                expList.add(expStr);
            }
            for (int i = 0; i < expList.size(); i++){

                String argType = arg_types.get(i);
                if (argType == "int")
                    argType = "i32";
                else if (argType == "boolean")
                    argType = "i1";
                else if (argType == "int[]")
                    argType = "i32*";
                else
                    argType = "i8*";
                
                emitString = emitString + ", " + argType + " " + expList.get(i);
            
            }
        }
        emitString = emitString + ")\n\n";
        emit(emitString);
        
        n.f5.accept(this, argu);
        return regCall;
    }



    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, String argu) throws Exception {
        String expType;
        this.methodParams.add("");
        expType = n.f0.accept(this, argu);
        // if (expType == "this") {
        //     expType = this.currentClass;
        // }
        int lastIndex = this.methodParams.size() - 1;
        this.methodParams.set(lastIndex, expType);
        
        n.f1.accept(this, argu);
        
        String copy = this.methodParams.get(lastIndex);
        this.methodParams.remove(lastIndex);
        return copy;

        
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, String argu) throws Exception {
        String expType;
        int lastIndex = this.methodParams.size() - 1;
        n.f0.accept(this, argu);
        expType = n.f1.accept(this, argu);
        // if (expType == "this") {
        //     expType = this.currentClass;
        // }
        this.methodParams.set(lastIndex, this.methodParams.get(lastIndex) + "," + expType);
        return null;
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
        this.messageSendClass = className;
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
                if( type == "int") {

                    
                }else if (type == "boolean") {
                    

                }else if (type == "int[]"){     //int array different load 
                    
                }else if (type == "boolean[]") {    //boolean array different load
                    
                }else {
                    this.messageSendClass = type;

                }
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
                    String var = "%" + id;
                    String reg = generateRegister();
                    String emitStr = "\t" + reg + " = load " + type + ", " + type + "* " + var + "\n\n";
                    emit(emitStr);
                    return reg;

                }else if (type == "int[]"){     //int array different load 
                    type = "i32*";
                }else if (type == "boolean[]") {    //boolean array different load
                    type = "i8*";
                }else {
                    this.messageSendClass = type;

                    type = "i8*";
                    String var = "%" + id;
                    String reg = generateRegister();
                    String emitStr = "\t" + reg + " = load " + type + ", " + type + "* " + var + "\n\n";
                    emit(emitStr);
                    return reg;
                }
                
            } else {

                if( type == "int") {

                    
                }else if (type == "boolean") {
                    

                }else if (type == "int[]"){     //int array different load 
                    
                }else if (type == "boolean[]") {    //boolean array different load
                    
                }else {
                    this.messageSendClass = type;

                }
                //TO BE CONTINUED
            }
        }
        

        return id;
    }

     /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, String argu) throws Exception {
        this.messageSendClass = "this";
        
        return n.f0.accept(this, argu);
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