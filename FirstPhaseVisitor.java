import syntaxtree.*;
import visitor.GJDepthFirst;
import parse_error.*;

public class FirstPhaseVisitor extends GJDepthFirst<String, SymbolTable> {

   String currentClass;



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
      System.out.println(name);
      this.currentClass = name;           //keep track of the class that is being "investigated"
      boolean entered = sTable.enter(name, false);
      if (!entered){
         throw new ParseError("Class with name " + name + " already declared");      
      }
      
      n.f2.accept(this, sTable);


      n.f3.accept(this, sTable);
      n.f4.accept(this, sTable);
      n.f5.accept(this, sTable);
      return _ret;
   }


   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   // public String visit(VarDeclaration n, SymbolTable sTable) {
   //    String _ret=null;
   //    String type, id;
   //    type = n.f0.accept(this, sTable);
   //    id = n.f1.accept(this, sTable);
   //    System.out.println(sTable + type + " " + id);
   //    n.f2.accept(this, sTable);
   //    return _ret;
   // }

   public String visit(NodeToken n, SymbolTable sTable) {    return n.toString(); }


   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, String argu) {
      return "boolean[]";
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
   // public String visit(MethodDeclaration n, SymbolTable sTable) {
   //    String _ret=null;
   //    String funName;
   //    n.f0.accept(this, sTable);
   //    n.f1.accept(this, sTable);
   //    funName = n.f2.accept(this, sTable);
   //    n.f3.accept(this, sTable);
   //    n.f4.accept(this, sTable);
   //    n.f5.accept(this, sTable);
   //    n.f6.accept(this, sTable);
   //    n.f7.accept(this, sTable);
   //    n.f8.accept(this, sTable);
   //    n.f9.accept(this, sTable);
   //    n.f10.accept(this, sTable);
   //    n.f11.accept(this, sTable);
   //    n.f12.accept(this, sTable);
   //    return _ret;
   // }

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