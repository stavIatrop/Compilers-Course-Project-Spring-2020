import syntaxtree.*;
import visitor.GJDepthFirst;
import parse_error.*;

public class FirstPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

   String currentClass = null;            //keep track of the class that is being "investigated"
   boolean classVar = false;              //keep track of the scope of the variable (class field or function variable)
   String currentMethod = null;           //keep track of the function that is being "investigated"


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
    public String visit(MainClass n, SymbolTable sTable) throws ParseError{
      String _ret=null;
      this.currentClass = "main";
      n.f0.accept(this, sTable);
      n.f1.accept(this, sTable);
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
      n.f14.accept(this, sTable);
      n.f15.accept(this, sTable);
      n.f16.accept(this, sTable);
      n.f17.accept(this, sTable);
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
    public String visit(ClassDeclaration n, SymbolTable sTable) throws ParseError{
      String _ret=null;
      String name;
      n.f0.accept(this, sTable);
      name = n.f1.accept(this, sTable);      //name = n.f1.f0.toString(); 	also works
      this.currentClass = name;           
      boolean entered = sTable.enter(null, name, false);
      if (!entered){
         throw new ParseError("Class with name " + name + " already declared");      
      }
      
      n.f2.accept(this, sTable);
      this.classVar = true;
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
   public String visit(ClassExtendsDeclaration n, SymbolTable sTable) throws ParseError{
      String _ret=null;
      String name;
      String parentClass;
      n.f0.accept(this, sTable);
      name = n.f1.accept(this, sTable);
      this.currentClass = name;
      n.f2.accept(this, sTable);
      parentClass = n.f3.accept(this, sTable);
      boolean parentDeclared = sTable.checkParent(parentClass);
      if (!parentDeclared) {
         throw new ParseError("Parent class " + parentClass + " has not been declared.");
      }
      boolean entered = sTable.enter(parentClass, name, false);
      if (!entered){
         throw new ParseError("Class with name " + name + " already declared.");      
      }
      n.f4.accept(this, sTable);
      this.classVar = true;
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
    public String visit(MethodDeclaration n, SymbolTable sTable) throws ParseError {
      String _ret=null;
      String funName;
      String return_type;
      n.f0.accept(this, sTable);
      return_type = n.f1.accept(this, sTable);
      funName = n.f2.accept(this, sTable);
      this.currentMethod = funName;

      ClassInfo cinfo = sTable.hmap.get(this.currentClass);
      if (cinfo.class_methods.containsKey(funName)) {
         throw new ParseError("Function name " + funName + " already declared in class " + this.currentClass);
      }
       
      cinfo.class_methods.put(funName, new FunInfo(return_type, false));          //Needs extra code for virtual

      n.f3.accept(this, sTable);
      n.f4.accept(this, sTable);
      
      boolean checkOver = sTable.checkOverriding(this.currentClass, this.currentMethod);
      if (!checkOver) {
         throw new ParseError("Overriding error at method " + this.currentMethod + " of class " + this.currentClass +
                     ": overriding method should have same name, return type and argument types as the overriden method.");
      }
      n.f5.accept(this, sTable);
      n.f6.accept(this, sTable);
      this.classVar = false;
      n.f7.accept(this, sTable);
      n.f8.accept(this, sTable);
      n.f9.accept(this, sTable);
      n.f10.accept(this, sTable);
      n.f11.accept(this, sTable);
      n.f12.accept(this, sTable);
      return _ret;
   }

/**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    public String visit(FormalParameterList n, SymbolTable sTable) throws ParseError{
      String _ret = null;
      String param;
      param = n.f0.accept(this, sTable);
      _ret = param + n.f1.accept(this, sTable);
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, SymbolTable sTable) throws ParseError{
      String type, id;
      type = n.f0.accept(this, sTable);
      id = n.f1.accept(this, sTable);
      if (this.currentClass != "main") {
         ClassInfo cinfo = sTable.hmap.get(this.currentClass);
         FunInfo finfo = cinfo.class_methods.get(this.currentMethod);
         if (finfo.arg_types.containsKey(id)) {
            throw new ParseError("Duplicate parameter " + id + " in function " + this.currentMethod);
         }
         finfo.arg_types.put(id, type);
      }
      
      return type + " " + id;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, SymbolTable sTable) throws ParseError{
      String _ret = null;
      n.f0.accept(this, sTable);
      _ret = ", " + n.f1.accept(this, sTable);
      return _ret;
   }
   
   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, SymbolTable sTable) throws ParseError {
      String type, id;
      type = n.f0.accept(this, sTable);
      id = n.f1.accept(this, sTable);
      ClassInfo cinfo = sTable.hmap.get(this.currentClass);
      if (classVar) {         //If variable declared is a class field
         
         if (cinfo.class_vars.containsKey(id)) {		//field with the same name already declared
            throw new ParseError("Field with name " + id + " in class " + this.currentClass + " already declared");
         }
         cinfo.class_vars.put(id, type);
         return type + " " + id;
      }
      //if variable declared is a method variable
      FunInfo finfo = cinfo.class_methods.get(this.currentMethod);
      if ( finfo.fun_vars.containsKey(id)) {        //variable with the same name already declared
         throw new ParseError("Duplicate local variable  " + id + " in function " + this.currentMethod);
         
      } else if (finfo.arg_types.containsKey(id)) {
         throw new ParseError("Local variable and function parameter have the same name " + id + " in function " + this.currentMethod);
      }
      finfo.fun_vars.put(id, type);
      n.f2.accept(this, sTable);
      return type + " " + id;
   }

   public String visit(NodeToken n, SymbolTable sTable) {    return n.toString(); }


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
    public String visit(IntegerArrayType n, SymbolTable sTable) throws ParseError{
      return "int[]";
   }
}