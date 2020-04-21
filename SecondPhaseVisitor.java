import visitor.GJDepthFirst;
import syntaxtree.*;

public class SecondPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

    String currentClass;
    String currentMethod;
    String expectedType;

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
        this.expectedType = return_type;
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
        this.expectedType = "boolean";
        //STOPPED HERE
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        n.f5.accept(this, sTable);
        n.f6.accept(this, sTable);
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
        if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name)) {
            throw new Exception(name + " cannot be resolved to a variable.");
        }
        
        n.f1.accept(this, sTable);
        this.expectedType = "int";
        n.f2.accept(this, sTable);
        n.f3.accept(this, sTable);
        n.f4.accept(this, sTable);
        if (funInfo.fun_vars.containsKey(name)) {
            String type = funInfo.fun_vars.get(name);
            this.expectedType = type;
        }else if (funInfo.arg_types.containsKey(name)) {
            String type = funInfo.arg_types.get(name);
            this.expectedType = type;
        }
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
    if (!funInfo.fun_vars.containsKey(name) && !funInfo.arg_types.containsKey(name)) {
        throw new Exception(name + " cannot be resolved to a variable.");
    }
    if (funInfo.fun_vars.containsKey(name)) {
        String type = funInfo.fun_vars.get(name);
        this.expectedType = type;
    }else if (funInfo.arg_types.containsKey(name)) {
        String type = funInfo.arg_types.get(name);
        this.expectedType = type;
    }

    n.f1.accept(this, sTable);
    n.f2.accept(this, sTable);
    n.f3.accept(this, sTable);
    return _ret;
 }

  /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, SymbolTable sTable) throws Exception {
        if (this.expectedType != "boolean") {
            throw new Exception("Type mismatch: cannot convert from boolean to " + this.expectedType);
        }
        String _ret=null;
        n.f0.accept(this, sTable);
        n.f1.accept(this, sTable);
        n.f2.accept(this, sTable);
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
    public String visit(PrimaryExpression n, SymbolTable sTable) throws Exception {
        
        System.out.println(n.f0.accept(this, sTable));
        return null;
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