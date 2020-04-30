package my_visitors;
import visitor.GJDepthFirst;

import java.util.ArrayList;

import syntaxtree.*;
import types.*;

public class SecondPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

    String currentClass;
    String currentMethod;
    boolean primaryExp;
    ArrayList<String> methodParams = new ArrayList<String>();

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
    public String visit(MainClass n, SymbolTable sTable) throws Exception {
        String name;
        n.f0.accept(this, sTable);
        name = n.f1.accept(this, sTable);
        this.currentClass = name;
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
        n.f6.accept(this, sTable);
        n.f7.accept(this, sTable);
        n.f8.accept(this, sTable);
        n.f9.accept(this, sTable);
        n.f10.accept(this, sTable);
        n.f11.accept(this, sTable);
        n.f12.accept(this, sTable);
        n.f13.accept(this, sTable);
        this.currentMethod = "main";
        n.f14.accept(this, sTable);
        n.f15.accept(this, sTable);
        n.f16.accept(this, sTable);
        n.f17.accept(this, sTable);
        return null;
    }
   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */

    public String visit(ClassDeclaration n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        this.currentClass = n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
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
    public String visit(ClassExtendsDeclaration n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        this.currentClass = n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
        n.f6.accept(this, sTable);
        n.f7.accept(this, sTable);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, SymbolTable sTable) throws Exception {
        String type;
        type = n.f0.accept(this, sTable);
        if (type != "int" && type != "boolean" && type != "int[]" && type != "boolean[]") {

            if (!sTable.checkClass(type)) {      //if class name is not defined before or after this var declaration
                throw new Exception(type + " cannot be resolved to a type");
            }
        }
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        return null;
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
    public String visit(MethodDeclaration n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        String return_type_correct;
        return_type_correct = n.f1.accept(this, sTable);
        this.currentMethod = n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
        n.f6.accept(this, sTable);
        n.f7.accept(this, sTable);
        n.f8.accept(this, sTable);
        n.f9.accept(this, sTable);
        String expType;
        expType = n.f10.accept(this, sTable);
        if (expType == "this") {
            expType = this.currentClass;
        }
        if ( expType != return_type_correct) {
            boolean isClassType1, isClassType2;
            isClassType1 = expType != "int" && expType != "int[]" && expType != "boolean" && expType != "boolean[]";
            isClassType2 = return_type_correct != "int" && return_type_correct != "int[]" && return_type_correct != "boolean" && return_type_correct != "boolean[]";
            if ( isClassType1 && isClassType2) {
                //ckeck for subtype, if expType has ancestor type, assignment is valid
                boolean isSubtype = sTable.checkSubtype(expType, return_type_correct);
                if (!isSubtype) {
                    throw new Exception("Type mismatch: cannot convert from " + expType + " to " + return_type_correct +
                                    " in return statement of " + this.currentMethod + " method of class " + this.currentClass);
                }
            }else {
                throw new Exception("Type mismatch: cannot convert from " + expType + " to " + return_type_correct +
                             " in return statement of " + this.currentMethod + " method of class " + this.currentClass);
            }
        }
        n.f11.accept(this, sTable);
        n.f12.accept(this, sTable);
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
    public String visit(IfStatement n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        String ExpType;
        ExpType = n.f2.accept(this, sTable);
        if (ExpType != "boolean") {
            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("If statement needs boolean expression not " + ExpType + ".");
        }
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
        n.f6.accept(this, sTable);
        return _ret;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        String ExpType;
        ExpType = n.f2.accept(this, sTable);
        if (ExpType != "boolean") {
            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("While statement needs boolean expression not " + ExpType + ".");
        }
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        return _ret;
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, SymbolTable sTable) throws Exception {
        String _ret=null;
        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        String ExpType;
        ExpType = n.f2.accept(this, sTable);
        if (ExpType != "int") {
            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("Print statement needs int expression not " + ExpType + ".");
        }
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        
        return _ret;
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
    public String visit(ArrayAssignmentStatement n, SymbolTable sTable) throws Exception {
        String _ret=null;
        String name;
        
        name = n.f0.accept(this, sTable);
        String type = sTable.lookupName(this.currentClass, this.currentMethod, name);
        if (type == null) {
            throw new Exception("Array variable " + name + " cannot be resolved to a variable.");
        }
        // ClassInfo cInfo = sTable.hmap.get(this.currentClass);
        // FunInfo funInfo = cInfo.class_methods.get(this.currentMethod);
        // ClassInfo parent;           //parent declaration outside if scope for later use if needed
        // boolean flag = false;

        // if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name) && !cInfo.class_vars.containsKey(name)) {
            
        //     boolean ancestors;
        //     if (cInfo.parentClass != null) {
        //         ancestors = true;
        //         parent = sTable.hmap.get(cInfo.parentClass);  //search for variable in ancestors
        //     }else {
        //         ancestors = false;
        //         parent = null;
        //     }
        //     while (ancestors) {

        //         if (parent.class_vars.containsKey(name)) {
        //             flag = true;
        //             break;
        //         }
        //         if (parent.parentClass != null) {
        //             parent = sTable.hmap.get(parent.parentClass);
        //         } else {
        //             ancestors = false;
        //         }
        //     }
        //     if (flag == false) {
        //         throw new Exception("Array variable " + name + " cannot be resolved to a variable.");
        //     }
        // } else {
        //     parent = null;
        // }
        
        n.f1.accept(this, sTable);

        String ExpType;
        ExpType = n.f2.accept(this, sTable);
        if (ExpType != "int") {
            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int.");
        }
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        // String type = "null";

        // if (flag == true) {         //it means that variable found on an ancestor's scope
        //     type = parent.class_vars.get(name);
            
        // }else { 

        //     if (funInfo.fun_vars.containsKey(name)) {       //first check method's scope
        //         type = funInfo.fun_vars.get(name);
                
        //     }else if (funInfo.arg_types.containsKey(name)) {
        //         type = funInfo.arg_types.get(name);
                
        //     } else if (cInfo.class_vars.containsKey(name)) {     //then class' and its ancestors' scopes
        //         type = cInfo.class_vars.get(name);
                    
        //     }
        // }

        if (type == "int" || type == "boolean") {
            throw new Exception("The type of the expression must be an array type but it resolved to " + type + ".");
        }
        if (type != "int[]" && type != "boolean[]") {
            throw new Exception("The type of the expression must be an array type but it resolved to " + type + ".");
        }
        String expectedType = "";
        if (type == "int[]") {
            expectedType = "int";
        }else if (type == "boolean[]") {
            expectedType = "boolean";
        }
            

        String expType2;
        expType2 = n.f5.accept(this, sTable);
        if (expType2 != expectedType) {
            if (expType2 == "this") {
                expType2 = this.currentClass;
            }
            throw new Exception("Type mismatch: cannot convert from " + expType2 + " to " + expectedType);
        }
        n.f6.accept(this, sTable);
        return _ret;
    }
    
    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, SymbolTable sTable) throws Exception {
        String _ret=null;
        String name;
        name = n.f0.accept(this, sTable);
        
        String type = sTable.lookupName(this.currentClass, this.currentMethod, name);
        if (type == null) {
            throw new Exception("Variable " + name + " cannot be resolved to a variable.");
        }
        // ClassInfo cInfo = sTable.hmap.get(this.currentClass);
        // FunInfo funInfo = cInfo.class_methods.get(this.currentMethod);
        // ClassInfo parent;           //parent declaration outside if scope for later use if needed
        // boolean flag = false;

        // if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name) && !cInfo.class_vars.containsKey(name)) {
            
        //     boolean ancestors;
        //     if (cInfo.parentClass != null) {
        //         ancestors = true;
        //         parent = sTable.hmap.get(cInfo.parentClass);  //search for variable in ancestors
        //     }else {
        //         ancestors = false;
        //         parent = null;
        //     }
        //     while (ancestors) {

        //         if (parent.class_vars.containsKey(name)) {
        //             flag = true;
        //             break;
        //         }
        //         if (parent.parentClass != null) {
        //             parent = sTable.hmap.get(parent.parentClass);
        //         } else {
        //             ancestors = false;
        //         }
        //     }
        //     if (flag == false) {
        //         throw new Exception("Variable " + name + " cannot be resolved to a variable.");
        //     }
        // } else {
        //     parent = null;
        // }
        
        // String type = "null";

        // if (flag == true) {         //it means that variable found on an ancestor's scope
        //     type = parent.class_vars.get(name);
            
        // }else { 

        //     if (funInfo.fun_vars.containsKey(name)) {       //first check method's scope
        //         type = funInfo.fun_vars.get(name);
                
        //     }else if (funInfo.arg_types.containsKey(name)) {
        //         type = funInfo.arg_types.get(name);
                
        //     } else if (cInfo.class_vars.containsKey(name)) {     //then class' and its ancestors' scopes
        //         type = cInfo.class_vars.get(name);
                    
        //     }
        // }
        

        n.f1.accept(this, sTable);
        String expType;
        expType = n.f2.accept(this, sTable);
        if (expType == "this") {
            expType = this.currentClass;
        }
        if (expType != type) {
            boolean isClassType1, isClassType2;
            isClassType1 = expType != "int" && expType != "int[]" && expType != "boolean" && expType != "boolean[]";
            isClassType2 = type != "int" && type != "int[]" && type != "boolean" && type != "boolean[]";
            if ( isClassType1 && isClassType2) {
                //ckeck for subtype, if expType has ancestor type, assignment is valid
                boolean isSubtype = sTable.checkSubtype(expType, type);
                if (!isSubtype) {
                    throw new Exception("Type mismatch: cannot convert from " + expType + " to " + type);

                }
            }else {
                throw new Exception("Type mismatch: cannot convert from " + expType + " to " + type);
            }
            
        }
        n.f3.accept(this, sTable);
        return _ret;
    }


     /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    public String visit(Expression n, SymbolTable sTable) throws Exception {
        return n.f0.accept(this, sTable);
    }

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, SymbolTable sTable) throws Exception {
        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        if (type1 != "boolean" || type2 != "boolean") {
            if (type1 == "this") {
                type1 = this.currentClass;
            }
            if (type2 == "this") {
                type2 = this.currentClass;
            }
            throw new Exception("The operator && is undefined for the argument type(s) " + type1 + ", " + type2);
        }
        return "boolean";
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, SymbolTable sTable) throws Exception {
        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        if (type1 != "int" || type2 != "int") {
            if (type1 == "this") {
                type1 = this.currentClass;
            }
            if (type2 == "this") {
                type2 = this.currentClass;
            }
            throw new Exception("The operator < is undefined for the argument type(s) " + type1 + ", " + type2);
        }
        return "boolean";
    }
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, SymbolTable sTable) throws Exception {
       
        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        // if (type1 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type1 == "int[]" || type1 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type1.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type1.substring(0, type1.length() - 2) + " object arrays.");
        // }
        // if (type1 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type1 != "int") {

        //     type1 = sTable.lookupName(this.currentClass, this.currentMethod, type1);
        // }

        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        // if (type2 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type2 == "int[]" || type2 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type2.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type2.substring(0, type2.length() - 2) + " object arrays.");
        // }
        // if (type2 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type2 != "int") {

        //     type2 = sTable.lookupName(this.currentClass, this.currentMethod, type2);
        // }

        if (type1 != "int" || type2 != "int") {
            if (type1 == "this") {
                type1 = this.currentClass;
            }
            if (type2 == "this") {
                type2 = this.currentClass;
            }
            throw new Exception("The operator + is undefined for the argument type(s) " + type1 + ", " + type2);
        }
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, SymbolTable sTable) throws Exception {

        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        // if (type1 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type1 == "int[]" || type1 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type1.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type1.substring(0, type1.length() - 2) + " object arrays.");
        // }
        // if (type1 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type1 != "int") {

        //     type1 = sTable.lookupName(this.currentClass, this.currentMethod, type1);
        // }
        
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        // if (type2 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type2 == "int[]" || type2 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type2.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type2.substring(0, type2.length() - 2) + " object arrays.");
        // }
        // if (type2 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type2 != "int") {

        //     type2 = sTable.lookupName(this.currentClass, this.currentMethod, type2);
        // }

        if (type1 != "int" || type2 != "int") {
            if (type1 == "this") {
                type1 = this.currentClass;
            }
            if (type2 == "this") {
                type2 = this.currentClass;
            }
            throw new Exception("The operator - is undefined for the argument type(s) " + type1 + ", " + type2);
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, SymbolTable sTable) throws Exception {

        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        // if (type1 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type1 == "int[]" || type1 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type1.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type1.substring(0, type1.length() - 2) + " object arrays.");
        // }
        // if (type1 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type1 != "int") {

        //     type1 = sTable.lookupName(this.currentClass, this.currentMethod, type1);
        // }
        
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        // if (type2 == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (type2 == "int[]" || type2 == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (type2.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + type2.substring(0, type2.length() - 2) + " object arrays.");
        // }
        // if (type2 == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (type2 != "int") {

        //     type2 = sTable.lookupName(this.currentClass, this.currentMethod, type2);
        // }

        if (type1 != "int" || type2 != "int") {
            if (type1 == "this") {
                type1 = this.currentClass;
            }
            if (type2 == "this") {
                type2 = this.currentClass;
            }
            throw new Exception("The operator * is undefined for the argument type(s) " + type1 + ", " + type2);
        }
        return "int";
    }
    

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, SymbolTable sTable) throws Exception {
        
        String PriType;
        PriType = n.f0.accept(this, sTable);
        if (PriType == "int" || PriType == "boolean") {
            throw new Exception("The type of the expression must be an array type but it resolved to " + PriType + ".");
        }
        if (PriType != "int[]" && PriType != "boolean[]") {
            if (PriType == "this") {
                PriType = this.currentClass;
            }
            throw new Exception("The type of the expression must be an array type but it resolved to " + PriType + ".");
        }
        // if (PriType == "int[]" || PriType == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (PriType.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + PriType.substring(0, PriType.length() - 2) + " object arrays.");
        // }
        

        n.f1.accept(this, sTable);
        String ExpType;
        ExpType = n.f2.accept(this, sTable);

        // if (ExpType == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (ExpType == "int[]" || ExpType == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }
        // if (ExpType.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
        //     throw new Exception("Only boolean or int arrays are permitted, not " + ExpType.substring(0, ExpType.length() - 2) + " object arrays.");
        // }
        // if (ExpType == "this") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        // }
        // if (ExpType != "int") {
        //     ExpType = sTable.lookupName(this.currentClass, this.currentMethod, ExpType);
        // }

        if (ExpType != "int") {
            throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int.");
        }
        n.f3.accept(this, sTable);
        String _ret = null;
        if (PriType == "int[]")
            _ret = "int";
        else if (PriType == "boolean[]") {
            _ret = "boolean";
        }
        return _ret;
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, SymbolTable sTable) throws Exception {

        String PriType;
        PriType = n.f0.accept(this, sTable);
        if (PriType == "int" || PriType == "boolean") {
            throw new Exception("The primitive type " + PriType + " does not have a field length");
        }
        if (PriType == "this" || (PriType != "int[]" && PriType != "boolean[]") ) {
            throw new Exception("length cannot be resolved or is not a field");
        }

        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, SymbolTable sTable) throws Exception {

        String PriType;
        PriType = n.f0.accept(this, sTable);
        
        n.f1.accept(this, sTable);
        
        String methodName;
        methodName = n.f2.accept(this, sTable);
        if (PriType == "int" || PriType == "boolean") {
            throw new Exception("Cannot invoke " + methodName + " on the primitive type " + PriType);
        }
        if (PriType == "int[]" || PriType == "boolean[]") {
            throw new Exception("Cannot invoke " + methodName + " on the array type " + PriType);
        }
        String className;
        if (PriType == "this") {
            className = this.currentClass;
        }
        // else if(PriType.contains("()")) {
        //     className = PriType.substring(0, PriType.length() - 2);
        // }
        else {         //check if class is declared

            if( sTable.checkClass(PriType)) {
                className = PriType;
            }else {
                throw new Exception("Cannot invoke " + methodName + " on the undeclared type " + PriType);
            }
        }
        FunInfo fInfo;
        fInfo = sTable.lookupMethod(className, methodName);
        if (fInfo == null) {
            throw new Exception("The method " + methodName + " is undefined for the type " + className);
        }
        ArrayList<String> methodPams = new ArrayList<String>();
        
        methodPams.addAll(fInfo.arg_types.values());

        n.f3.accept(this, sTable);
        String args;
        args = n.f4.accept(this, sTable);
        if (args == null) {
            if(methodPams.size() != 0) {
                throw new Exception("The method " + methodName + " is not applicable for the arguments (too few arguments)");
            }
        }else {

            ArrayList<String> expList = new ArrayList<String>();
            int j = 0;
            if (args.contains(",")) {
                int i;
                for (i = 0; i < args.length(); i++) {
                    if (args.charAt(i) == ',') {
                        String sub;
                        sub = args.substring(j, i);
                        j = i + 1;
                        expList.add(sub);
                    }
                }
                String sub;
                sub = args.substring(j, i);
                expList.add(sub);
            }else {
                expList.add(args);
            }
            
            if (!expList.equals(methodPams) ) {

                if (expList.size() > methodPams.size()) {
                    throw new Exception("The method " + methodName + " is not applicable for the arguments " + expList);
                }else if (expList.size() < methodPams.size()) {
                    throw new Exception("The method " + methodName + " is not applicable for the arguments " + expList);
                }
                //same number of arguments, check for subtypes
                int i, length;
                length = expList.size();
                for (i = 0; i < length; i++) {
                    String param = methodPams.get(i);
                    String callArg = expList.get(i);
                    boolean isClassType1, isClassType2;
                    isClassType1 = callArg != "int" && callArg != "int[]" && callArg != "boolean" && callArg != "boolean[]";
                    isClassType2 = param != "int" && param != "int[]" && param != "boolean" && param != "boolean[]"; 
                    if ( isClassType1 && isClassType2 ) {
                        boolean isSubtype = sTable.checkSubtype(callArg, param);
                        if (!isSubtype) {   //if one call argument at least is not a subtype, throw exception
                            throw new Exception("The method " + methodName + " is not applicable for the arguments " + expList);
                        }
                    }else {
                        if (param != callArg) {
                            throw new Exception("The method " + methodName + " is not applicable for the arguments " + expList);
                        }
                    }
                }
            }
        }
        
        n.f5.accept(this, sTable);
        String return_type = fInfo.return_type;
        return return_type;
    }


    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, SymbolTable sTable) throws Exception {
        String expType;
        this.methodParams.add("");
        expType = n.f0.accept(this, sTable);
        if (expType == "this") {
            expType = this.currentClass;
        }
        int lastIndex = this.methodParams.size() - 1;
        this.methodParams.set(lastIndex, expType);
        
        n.f1.accept(this, sTable);
        
        String copy = this.methodParams.get(lastIndex);
        this.methodParams.remove(lastIndex);
        return copy;

        
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, SymbolTable sTable) throws Exception {
        String expType;
        int lastIndex = this.methodParams.size() - 1;
        n.f0.accept(this, sTable);
        expType = n.f1.accept(this, sTable);
        if (expType == "this") {
            expType = this.currentClass;
        }
        this.methodParams.set(lastIndex, this.methodParams.get(lastIndex) + "," + expType);
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
    public String visit(PrimaryExpression n, SymbolTable sTable) throws Exception {
        this.primaryExp = true;
        String s = n.f0.accept(this, sTable);
        this.primaryExp = false;
        return s;
    }
    
    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, SymbolTable sTable) throws Exception {
        String id;
        id = n.f0.accept(this, sTable);
        if (this.primaryExp == true) {
            String typeId;
            typeId = sTable.lookupName(this.currentClass, this.currentMethod, id);
            if (typeId == null) {
                throw new Exception("Variable " + id + " cannot be resolved to a variable.");
    
            }
            
            return typeId;
        }
        return id;
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, SymbolTable sTable) throws Exception {
        return "int";
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, SymbolTable sTable) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, SymbolTable sTable) throws Exception {
        return "boolean";
    }
    

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, SymbolTable sTable) throws Exception {
        
        return "this";
    }

    /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(BooleanArrayAllocationExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        String ExpType;
        ExpType = n.f3.accept(this, sTable);
        // if (ExpType == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (ExpType == "int[]" || ExpType == "boolean[]") {
        //     throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int in boolean array allocation.");
        // }

        // if (ExpType != "int") {
        //     ExpType = sTable.lookupName(this.currentClass, this.currentMethod, ExpType);            
        // }

        if (ExpType != "int") {

            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int in boolean array allocation.");
        }
        n.f4.accept(this, sTable);
        return "boolean[]";
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(IntegerArrayAllocationExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        String ExpType;
        ExpType = n.f3.accept(this, sTable);
        // if (ExpType == "boolean") {
        //     throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        // }
        // if (ExpType == "int[]" || ExpType == "boolean[]") {
        //     throw new Exception("Only single dimension arrays are permitted.");
        // }

        // if (ExpType != "int") {
        //     ExpType = sTable.lookupName(this.currentClass, this.currentMethod, ExpType);            
        // }
        if (ExpType != "int") {
            if (ExpType == "this") {
                ExpType = this.currentClass;
            }
            throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int in integer array allocation.");
        }
        n.f4.accept(this, sTable);
        return "int[]";
    }


    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        String className;
        this.primaryExp = false;
        className = n.f1.accept(this, sTable);
        if (!sTable.hmap.containsKey(className)) {
            throw new Exception(className + " class allocation cannot be resolved to a type.");
        }
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        return className;
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        return "boolean";
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, SymbolTable sTable) throws Exception {
        n.f0.accept(this, sTable);
        String ExpType;
        ExpType = n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        return ExpType;
    }

    public String visit(NodeToken n, SymbolTable sTable) {    
        
        return n.toString(); 
    }

    /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(BooleanArrayType n, SymbolTable sTable) {
        return "boolean[]";
    } 

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(IntegerArrayType n, SymbolTable sTable) throws Exception{
        return "int[]";
    }
  
}