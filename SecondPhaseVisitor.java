import visitor.GJDepthFirst;
import syntaxtree.*;

public class SecondPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

    String currentClass;
    String currentMethod;

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
        n.f1.accept(this, sTable);
        this.currentMethod = n.f2.accept(this, sTable);
        ClassInfo cInfo = sTable.hmap.get(this.currentClass);
        FunInfo funInfo = cInfo.class_methods.get(this.currentMethod);
        String return_type = funInfo.return_type;
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
        ClassInfo cInfo = sTable.hmap.get(this.currentClass);
        FunInfo funInfo = cInfo.class_methods.get(this.currentMethod);
        ClassInfo parent;           //parent declaration outside if scope for later use if needed
        boolean flag = false;

        if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name) && !cInfo.class_vars.containsKey(name)) {
            
            boolean ancestors;
            if (cInfo.parentClass != null) {
                ancestors = true;
                parent = sTable.hmap.get(cInfo.parentClass);  //search for variable in ancestors
            }else {
                ancestors = false;
                parent = null;
            }
            while (ancestors) {

                if (parent.class_vars.containsKey(name)) {
                    flag = true;
                    break;
                }
                if (parent.parentClass != null) {
                    parent = sTable.hmap.get(parent.parentClass);
                } else {
                    ancestors = false;
                }
            }
            if (flag == false) {
                throw new Exception("Array variable " + name + " cannot be resolved to a variable.");
            }
        } else {
            parent = null;
        }
        
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        String type = "null";

        if (flag == true) {         //it means that variable found on an ancestor's scope
            type = parent.class_vars.get(name);
            
        }else { 

            if (funInfo.fun_vars.containsKey(name)) {       //first check method's scope
                type = funInfo.fun_vars.get(name);
                
            }else if (funInfo.arg_types.containsKey(name)) {
                type = funInfo.arg_types.get(name);
                
            } else if (cInfo.class_vars.containsKey(name)) {     //then class' and its ancestors' scopes
                type = cInfo.class_vars.get(name);
                    
            }
        }
        String expectedType = type.substring(0, type.length() - 2);

        
        n.f5.accept(this, sTable);
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
        ClassInfo cInfo = sTable.hmap.get(this.currentClass);
        FunInfo funInfo = cInfo.class_methods.get(this.currentMethod);
        ClassInfo parent;           //parent declaration outside if scope for later use if needed
        boolean flag = false;

        if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name) && !cInfo.class_vars.containsKey(name)) {
            
            boolean ancestors;
            if (cInfo.parentClass != null) {
                ancestors = true;
                parent = sTable.hmap.get(cInfo.parentClass);  //search for variable in ancestors
            }else {
                ancestors = false;
                parent = null;
            }
            while (ancestors) {

                if (parent.class_vars.containsKey(name)) {
                    flag = true;
                    break;
                }
                if (parent.parentClass != null) {
                    parent = sTable.hmap.get(parent.parentClass);
                } else {
                    ancestors = false;
                }
            }
            if (flag == false) {
                throw new Exception("Variable " + name + " cannot be resolved to a variable.");
            }
        } else {
            parent = null;
        }
        
        String type = "null";

        if (flag == true) {         //it means that variable found on an ancestor's scope
            type = parent.class_vars.get(name);
            
        }else { 

            if (funInfo.fun_vars.containsKey(name)) {       //first check method's scope
                type = funInfo.fun_vars.get(name);
                
            }else if (funInfo.arg_types.containsKey(name)) {
                type = funInfo.arg_types.get(name);
                
            } else if (cInfo.class_vars.containsKey(name)) {     //then class' and its ancestors' scopes
                type = cInfo.class_vars.get(name);
                    
            }
        }
        

        n.f1.accept(this, sTable);
        System.out.println("name:" + name + " type: " + type );
        String s = n.f2.accept(this, sTable);
        System.out.println("EXPR REEA : " +  s);
        n.f3.accept(this, sTable);
        return _ret;
    }

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        return "boolean";
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);

        return "boolean";
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
        //String s = n.f0.accept(this, sTable);
        return n.f0.accept(this, sTable);
    }
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, SymbolTable sTable) throws Exception {
       
        String type1, type2;
        type1 = n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        if (type1 != "int" || type2 != "int") {
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
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        if (type1 != "int" || type2 != "int") {
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
        n.f1.accept(this, sTable);
        type2 = n.f2.accept(this, sTable);
        if (type1 != "int" || type2 != "int") {
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
        if (PriType == "int") {
            throw new Exception("The type of the expression must be an array type but it resolved to int");
        }
        if (PriType == "boolean") {
            throw new Exception("The type of the expression must be an array type but it resolved to boolean");
        }
        if (PriType == "int[]" || PriType == "boolean[]") {
            throw new Exception("Only single dimension arrays are permitted.");
        }
        if (PriType.contains("()") ) {            //LOGIKA THE THA MPEI POTE EDV ALLA TO AFHNV PROS TO PARON
            throw new Exception("Only boolean or int arrays are permitted, not " + PriType.substring(0, PriType.length() - 2) + " object arrays.");
        }
        if (PriType == "this") {
            throw new Exception("The type of the expression must be an array type but it resolved to " + this.currentClass + ".");
        }

        ClassInfo cinfo;
        cinfo = sTable.hmap.get(this.currentClass);
        FunInfo funInfo = cinfo.class_methods.get(this.currentMethod);  //search the identifier PriType to current method's scope

        
        n.f1.accept(this, sTable);
        String ExpType;
        ExpType = n.f2.accept(this, sTable);
        if (ExpType != "int") {
            throw new Exception("Type mismatch: cannot convert from " + ExpType + " to int in integer array allocation.");
        }
        n.f3.accept(this, sTable);
        return null;
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, SymbolTable sTable) throws Exception {

        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
        return "int";
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

        String s = n.f0.accept(this, sTable);
        System.out.println(s);
        return s;
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
        if (ExpType != "int") {
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
        if (ExpType != "int") {
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
        className = n.f1.accept(this, sTable);
        if (!sTable.hmap.containsKey(className)) {
            throw new Exception(className + " class allocation cannot be resolved to a type.");
        }
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        return className + "()";
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