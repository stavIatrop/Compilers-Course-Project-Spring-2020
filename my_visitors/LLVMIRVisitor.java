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
    public Integer label;
    public boolean primaryExp;
    public String messageSendClass;
    ArrayList<String> methodParams = new ArrayList<String>();
    LinkedHashMap<String, String> registerTypes = new LinkedHashMap<String, String>();

    public LLVMIRVisitor(SymbolTable stable, VTable vtables, File file) {
        this.sTable = stable;
        this.vTables = vtables;
        this.LLVMfile = file;
        this.register = 0;
        this.label = 0;
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
        this.registerTypes.clear();
        this.register = 0;
        this.label = 0;
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

        if (!fInfo.arg_types.isEmpty()) {
            for (String arg : fInfo.arg_types.keySet()) {

                String type = fInfo.arg_types.get(arg);

                //perform alloca for function parameters
                if (type == "int") {
                    emitStr = "\t%" + arg + " = alloca " + "i32\n";
                    emitStr = emitStr + "\tstore i32 %." + arg + ", i32* %" + arg + "\n\n";
                }else if (type == "boolean") {
                    emitStr = "\t%" + arg + " = alloca " + "i1\n";
                    emitStr = emitStr + "\tstore i1 %." + arg + ", i1* %" + arg + "\n\n";
                }else if (type == "int[]") {
                    emitStr = "\t%" + arg + " = alloca " + "i32*\n";
                    emitStr = emitStr + "\tstore i32* %." + arg + ", i32** %" + arg + "\n\n";

                }else {
                    emitStr = "\t%" + arg + " = alloca " + "i8*\n";
                    emitStr = emitStr + "\tstore i8* %." + arg + ", i8** %" + arg + "\n\n";

                }

                emit(emitStr);

            }
        }
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        this.classVar = false;
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        String expType;
        expType = n.f10.accept(this, argu);
        // if (expType == "true") {
        //     expType = "1";
        // }else if (expType == "false") {
        //     expType = "0";
        // }
        emitStr = "\tret " + retType + " " + expType + "\n";
        emit(emitStr);

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
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, String argu) throws Exception {
        String _ret=null;
        String[] labels = generateLabel("while");
        emit("\tbr label %" + labels[0] + "\n\n");
        emit(labels[0] + ":\n");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String exp;
        exp = n.f2.accept(this, argu);
        String emitString = "\tbr i1 " + exp + ", label %" + labels[1] +  ", label %" + labels[2] + "\n\n";
        emit(emitString);
        emit(labels[1] + ":\n");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        emit("\tbr label %" + labels[0] + "\n\n" + 
            labels[2] + ":\n");
        return _ret;
    }

    /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String exp;
        exp = n.f2.accept(this, argu);
        String[] labels = generateLabel("if");
        String emitString = "\tbr i1 " + exp + ", label %" + labels[0] +", label %" + labels[1] + "\n\n";
        emit(emitString);

        emit(labels[0] + ":\n");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        emit("\tbr label " + "%" + labels[2] + "\n\n");
        n.f5.accept(this, argu);
        emit(labels[1] + ":\n");
        n.f6.accept(this, argu);
        emit("\tbr label " + "%" + labels[2] + "\n\n");
        emit(labels[2] + ":\n");
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
        

        n.f1.accept(this, argu);
        String exp;
        exp = n.f2.accept(this, argu);

        String var = "";
        String emitStr;
        if (scope == "class") {

            Integer offset = vTables.findOffset(this.currentClass, id);
            String regGetElem = generateRegister();
            emitStr = "\t" + regGetElem + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
            emit(emitStr);
            String regBitcast = generateRegister();
            emitStr = "\t" + regBitcast + " = bitcast i8* " + regGetElem + " to " + type + "*" + "\n\n";
            emit(emitStr);  
            var = regBitcast;
            
        }else if (scope == "fun_var" || scope == "arg") {
            var = "%" + id;
        }

        // if (exp == "true") {
        //     exp = "1";
        // }else if (exp == "false"){
        //     exp = "0";
        // }
        emitStr = "\tstore " + type + " " + exp + ", " + type + "* " + var + "\n\n";
        if (!emit(emitStr)) {
            throw new Exception("Something went wrong while compiling assignment statement of " + id + " variable");
        }
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, String argu) throws Exception {
        String clause1, clause2;
        clause1 = n.f0.accept(this, argu);
        //Check first if clause1 is false
        String[] labels = generateLabel("and");
        String emitString = "\tbr i1 " + clause1 + ", label %" + labels[1] + ", label %" + labels[0] + "\n\n";
        emit(emitString);
        
        emit(labels[0]  + ":\n" +
            "\tbr label %" + labels[2] + "\n\n" + 
            labels[1] + ":\n");

        n.f1.accept(this, argu);
        clause2 = n.f2.accept(this, argu);

        emit("\tbr label %" + labels[2] + "\n\n" +
            labels[2]  + ":\n" +
            "\tbr label %" + labels[3] + "\n\n" + 
            labels[3] + ":\n");
        
        String regPhi = generateRegister();
        emitString = "\t" + regPhi + " = phi i1  [ 0, %" + labels[0] + " ], [ " + clause2 + ", %" + labels[2] + " ]\n\n";  
        emit(emitString);

        return regPhi;
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, String argu) throws Exception {
        n.f0.accept(this, argu);
        String clause;
        clause = n.f1.accept(this, argu);
        String regXor = generateRegister();
        String emitString = "\t" + regXor + " = xor i1 1, " + clause + "\n";
        emit(emitString);
        return regXor;
    }
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, String argu) throws Exception {
        String priExp1, priExp2;
        priExp1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        priExp2 = n.f2.accept(this, argu);
        String regCmp = generateRegister();
        String emitStr = "\t" + regCmp + " = icmp slt i32 " + priExp1 + ", " + priExp2 + "\n";
        emit(emitStr);
        return regCmp;
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
        String regPrimExp;
        regPrimExp = n.f0.accept(this, argu);
        String emitString;
        String regBitcast = generateRegister();
        emitString = "\t" + regBitcast + " = bitcast i8* " + regPrimExp + " to i8***\n";
        emit(emitString);
        String regLoad = generateRegister();
        emitString = "\t" + regLoad + " = load i8**, i8*** " + regBitcast + "\n";
        emit(emitString);
        
        n.f1.accept(this, argu);
        String methodName;
        methodName = n.f2.accept(this, argu);
        String regGetElem = generateRegister();

        Integer methodOffset = vTables.findMethodOffset(this.messageSendClass, methodName);
        emitString = "\t" + regGetElem + " = getelementptr i8*, i8** " + regLoad + ", i32 " + methodOffset + "\n";
        emit(emitString);
        String regLoad2 = generateRegister();
        emitString = "\t" + regLoad2 + " = load i8*, i8** " + regGetElem + "\n";
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
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, String argu) throws Exception {
        String priExp1 ,priExp2;
        priExp1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        priExp2 = n.f2.accept(this, argu);
        String regAdd = generateRegister();
        String emitStr = "\t" + regAdd + " = add i32 " + priExp1 + ", " + priExp2 + "\n\n";
        emit(emitStr);
        return regAdd;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, String argu) throws Exception {
        String priExp1 ,priExp2;
        priExp1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        priExp2 = n.f2.accept(this, argu);
        String regMinus = generateRegister();
        String emitStr = "\t" + regMinus + " = sub i32 " + priExp1 + ", " + priExp2 + "\n\n";
        emit(emitStr);
        return regMinus;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, String argu) throws Exception {
        String priExp1 ,priExp2;
        priExp1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        priExp2 = n.f2.accept(this, argu);
        String regMul = generateRegister();
        String emitStr = "\t" + regMul + " = mul i32 " + priExp1 + ", " + priExp2 + "\n\n";
        emit(emitStr);
        return regMul;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, String argu) throws Exception {
        
        String priExp1;
        priExp1 = n.f0.accept(this, argu);
        
        String type = this.registerTypes.get(priExp1);
        if (type == "int[]") {
            type = "i32";
        }else {
            type = "i8";
        }
        String regSizeArray = generateRegister();
        String emitString;
        emitString = "\t" + regSizeArray + " = load i32, " + type + "* " + priExp1 + "\n";
        emit(emitString);
        
        n.f1.accept(this, argu);
        String priExp2;
        priExp2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        String regCheckGEZero = generateRegister();
        emitString = "\t" + regCheckGEZero + " = icmp sge i32 " + priExp2 + ", 0\n";
        emit(emitString);

        String regCheckLTSize = generateRegister();
        emitString = "\t" + regCheckLTSize + " = icmp slt i32 " + priExp2 + ", " + regSizeArray + "\n";
        emit(emitString);

        
        String regAnd = generateRegister();
        emitString = "\t" + regAnd + " = and i1 " + regCheckGEZero + ", " + regCheckLTSize + "\n";
        emit(emitString);

        String[] labels = generateLabel("oob");
        emitString = "\tbr i1 " + regAnd + ", label %" + labels[1] + ", label %" + labels[0] + "\n\n";
        emit(emitString);

        emit(labels[0] + ":\n" +
            "\tcall void @throw_oob()\n" +
            "\tbr label %" + labels[1] + "\n\n" +
            labels[1] + ":\n");
        
        String regIndex = generateRegister();
        emitString = "\t" + regIndex + " = add i32 1, " + priExp2 + "\n";
        emit(emitString);

        String regGetElem = generateRegister();
        emitString = "\t" + regGetElem + " = getelementptr " + type + ", " + type + "* " + priExp1 + ", i32 " + regIndex + "\n";
        emit(emitString); 

        String regLoad = generateRegister();
        emitString = "\t" + regLoad + " = load " + type + ", " + type + "* " + regGetElem + "\n\n";
        emit(emitString);

        return regLoad;
    }


    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String id;
        id = n.f0.accept(this, argu);
        String[] ret = sTable.lookupNameScope(this.currentClass, this.currentMethod, id);
        if (ret == null) {
            throw new Exception("Name " + id + " is not declared.");
        }
        String type = ret[0];
        if (type == "int[]") {
            type = "i32";
        }else {                 //NEEDS EXTRA CODE FOR BOOLEAN ARRAYS
            type = "i8";
        }
        String scope = ret[1];
        String regLoad;
        String emitString;
        if ( scope == "class") {
            Integer offset = vTables.findOffset(this.currentClass, id);
            String regGetElem = generateRegister();
            emitString = "\t" + regGetElem + " = getelemtptr i8, i8* %this, i32 " + offset + "\n";
            emit(emitString);

            String regBitcast = generateRegister();
        
            emitString = "\t" + regBitcast + " = i8* " + regGetElem + " to " + type + "**\n\n";
            emit(emitString);
            regLoad = generateRegister();
            emitString = "\t" + regLoad + " = load " + type + "*, " + type + "** " + regBitcast + "\n";
        }else {
            regLoad = generateRegister();
            emitString = "\t" + regLoad + " = load " + type + "*, " + type + "** " + "%" + id + "\n";
            emit(emitString);
        }

        String expIndex, exp;

        n.f1.accept(this, argu);
        expIndex = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        exp = n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        String regSizeArray = generateRegister();
        emitString = "\t" + regSizeArray + " = load i32, " + type + "* " + regLoad + "\n";
        emit(emitString);

        String regCheckGEZero = generateRegister();
        emitString = "\t" + regCheckGEZero + " = icmp sge i32 " + expIndex + ", 0\n";
        emit(emitString);

        String regCheckLTSize = generateRegister();
        emitString = "\t" + regCheckLTSize + " = icmp slt i32 " + expIndex + ", " + regSizeArray + "\n";
        emit(emitString);

        
        String regAnd = generateRegister();
        emitString = "\t" + regAnd + " = and i1 " + regCheckGEZero + ", " + regCheckLTSize + "\n";
        emit(emitString);

        String[] labels = generateLabel("oob");
        emitString = "\tbr i1 " + regAnd + ", label %" + labels[1] + ", label %" + labels[0] + "\n\n";
        emit(emitString);

        emit(labels[0] + ":\n" +
            "\tcall void @throw_oob()\n" +
            "\tbr label %" + labels[1] + "\n\n" +
            labels[1] + ":\n");
        
        String regIndex = generateRegister();
        emitString = "\t" + regIndex + " = add i32 1, " + expIndex + "\n";
        emit(emitString);

        String regGetElem = generateRegister();
        emitString = "\t" + regGetElem + " = getelementptr " + type + ", " + type + "* " + regLoad + ", i32 " + regIndex + "\n";
        emit(emitString); 

        emitString = "\tstore " + type + " " + exp + ", " + type + "* " + regGetElem + "\n\n";
        emit(emitString);

        return null;
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
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(BooleanArrayAllocationExpression n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(IntegerArrayAllocationExpression n, String argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String exp;
        String regAdd = generateRegister();
        
        exp = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        String emitString = "\t" + regAdd + " = add i32 1, " + exp + "\n";
        emit(emitString);

        String regCmp = generateRegister();
        emitString = "\t" + regCmp + " = icmp sge i32 " + regAdd + ", 1\n";
        emit(emitString);
        String[] labels = generateLabel("nsz");
        emitString = "\tbr i1 " + regCmp + ", label %" + labels[1] + ", label %" + labels[0] + "\n\n";
        emit(emitString);
        
        emit(labels[0] + ":\n");
        emit("\tcall void @throw_nsz()\n" +
                "\tbr label %" + labels[1] + "\n\n");
        emit(labels[1] + ":\n");
        String regAlloc = generateRegister();
        emitString = "\t" + regAlloc + " = call i8* @calloc(i32 " + regAdd + ", i32 4)\n";
        emit(emitString);
        String regBitcast = generateRegister();
        emitString = "\t" + regBitcast + " = bitcast i8* " + regAlloc + " to i32*\n";
        emit(emitString);
        emitString = "\tstore i32 " + exp + ", i32* " + regBitcast + "\n\n";
        emit(emitString); 
        return regBitcast;
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
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, String argu) throws Exception {
        String exp;
        n.f0.accept(this, argu);
        exp = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return exp;
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
                String emitStr;
                if( type == "int") {

                    String regGetElem = generateRegister();
                    emitStr = "\t" + regGetElem + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
                    emit(emitStr);
                    String regBitcast = generateRegister();
                    emitStr = "\t" + regBitcast + " = bitcast i8* " + regGetElem + " to i32*" + "\n";
                    emit(emitStr);
                    String regLoad = generateRegister();
                    
                    emitStr = "\t" + regLoad + " = load i32, i32* " + regBitcast + "\n\n";
                    emit(emitStr);
                    return regLoad;

                }else if (type == "boolean") {
                    
                    String regGetElem = generateRegister();
                    emitStr = "\t" + regGetElem + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
                    emit(emitStr);
                    String regBitcast = generateRegister();
                    emitStr = "\t" + regBitcast + " = bitcast i8* " + regGetElem + " to i1*" + "\n";
                    emit(emitStr);
                    String regLoad = generateRegister();
                    emitStr = "\t" + regLoad + " = load i1, i1* " + regBitcast + "\n\n";
                    emit(emitStr);
                    return regLoad;

                }else if (type == "int[]"){     //int array different load 
                    
                    String regGetElem = generateRegister();
                    emitStr = "\t" + regGetElem + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
                    emit(emitStr);
                    String regBitcast = generateRegister();
                    emitStr = "\t" + regBitcast + " = bitcast i8* " + regGetElem + " to i32**" + "\n";
                    emit(emitStr);
                    String regLoad = generateRegister();
                    this.registerTypes.put(regLoad, "int[]");   //keep track of type for the array lookup

                    emitStr = "\t" + regLoad + " = load i32*, i32** " + regBitcast + "\n\n";
                    emit(emitStr);
                    return regLoad;

                }else if (type == "boolean[]") {    //boolean array different load, NOT COMPLETED
                    
                }else {
                    this.messageSendClass = type;
                    String regGetElem = generateRegister();
                    emitStr = "\t" + regGetElem + " = getelementptr i8, i8* %this, i32 " + offset + "\n";
                    emit(emitStr);
                    String regBitcast = generateRegister();
                    emitStr = "\t" + regBitcast + " = bitcast i8* " + regGetElem + " to i8**" + "\n";
                    emit(emitStr);
                    String regLoad = generateRegister();
                    emitStr = "\t" + regLoad + " = load i8*, i8** " + regBitcast + "\n\n";
                    emit(emitStr);
                    return regLoad;
                }
                //TO BE CONTINUED

            }else if (scope == "fun_var" || scope == "arg") {     //load the fun var
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
                    String var = "%" + id;
                    String reg = generateRegister();
                    String emitStr = "\t" + reg + " = load " + type + ", " + type + "* " + var + "\n\n";
                    this.registerTypes.put(reg, "int[]");   //keep track of type for the array lookup

                    emit(emitStr);
                    return reg;
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
             
            }
        }
        

        return id;
    }

     /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, String argu) throws Exception {
        this.messageSendClass = this.currentClass;
        return "%this";
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, String argu) throws Exception {
        return "1";
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, String argu) throws Exception {
        return "0";
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

    public String[] generateLabel(String exp){
        
        String[] labels = null;

        if (exp == "if") {
            labels = new String[3];
            labels[0] = "if_then_" + this.label.toString();
            labels[1] = "if_else_" + this.label.toString();
            labels[2] = "if_end_" + this.label.toString();
        }else if (exp == "nsz") {
            labels = new String[2];
            labels[0] = "nsz_err_" + this.label;
            labels[1] = "nsz_ok_" + this.label;
        }else if ( exp == "oob") {
            labels = new String[2];
            labels[0] = "oob_err_" + this.label.toString();
            labels[1] = "oob_ok_"  + this.label.toString();
        }else if ( exp == "and") {
            labels = new String[4];
            labels[0] = "and_clause_" + this.label.toString();
            this.label += 1;
            labels[1] = "and_clause_" + this.label.toString();
            this.label += 1;
            labels[2] = "and_clause_" + this.label.toString();
            this.label += 1;
            labels[3] = "and_clause_" + this.label.toString();
        } else if ( exp == "while") {
            labels = new String[3];
            labels[0] = "loop" + this.label.toString();
            this.label += 1;
            labels[1] = "loop" + this.label.toString();
            this.label += 1;
            labels[2] = "loop" + this.label.toString();
        }
        this.label += 1;
        return labels;
    }
}