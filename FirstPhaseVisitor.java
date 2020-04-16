import syntaxtree.*;
import visitor.GJDepthFirst;
import parse_error.*;

public class FirstPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

   String currentClass;
   boolean classVar;
   String currentMethod;



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
      this.currentClass = name;           //keep track of the class that is being "investigated"
      boolean entered = sTable.enter(name, false);
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
      n.f3.accept(this, sTable);
      //Stopped HERE!
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
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    public String visit(FormalParameterList n, SymbolTable sTable) throws ParseError{
      String _ret=null;
      _ret = n.f0.accept(this, sTable);
      _ret = _ret + n.f1.accept(this, sTable);
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
   // public String visit(VarDeclaration n, SymbolTable sTable) throws ParseError {
   //    String _ret=null;
   //    String type, id;
   //    type = n.f0.accept(this, sTable);
   //    id = n.f1.accept(this, sTable);
   //    ClassInfo cinfo = sTable.hmap.get(this.currentClass);
   //    if (classVar) {         //If variable declared is a class field
         
   //       if (cinfo.class_vars.containsKey(id)) {		//field with the same name already declared
   //          throw new ParseError("Field with name " + id + " in class " + this.currentClass + " already declared");
   //       }
   //       cinfo.class_vars.put(id, new VarInfo(type, null));

   //    }
   //    //if variable declared is a method variable
   //    FunInfo finfo = cinfo.class_methods.get(this.currentMethod);
   //    if (finfo.fun_vars.containsKey(id)) {        //variable with the same name already declared
   //       throw new ParseError("Duplicate local variable  " + id + " in function " + this.currentMethod);
         
   //    } else if (finfo.arg_types.containsKey(id)) {
   //       throw new ParseError("Local variable and function parameter have the same name " + id + " in function " + this.currentMethod);
   //    }
   //    finfo.fun_vars.put(id, new VarInfo(type, null));
   //    n.f2.accept(this, sTable);
   //    return _ret;
   // }

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
   // public String visit(ClassExtendsDeclaration n, SymbolTable sTable) {
   //    String _ret=null;
   //    String name;
   //    n.f0.accept(this, sTable);
   //    name = n.f1.accept(this, sTable);
   //    n.f2.accept(this, sTable);
   //    n.f3.accept(this, sTable);
   //    n.f4.accept(this, sTable);
   //    n.f5.accept(this, sTable);
   //    n.f6.accept(this, sTable);
   //    n.f7.accept(this, sTable);
   //    return _ret;
   // }
}