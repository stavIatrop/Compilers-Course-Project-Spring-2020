import syntaxtree.*;
import visitor.GJDepthFirst;

public class FirstPhaseVisitor extends GJDepthFirst<String, String> {

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, String argu) {
      String _ret=null;
      String type, id;
      type = n.f0.accept(this, argu);
      id = n.f1.accept(this, argu);
      System.out.println(argu + type + " " + id);
      n.f2.accept(this, argu);
      return _ret;
   }

   public String visit(NodeToken n, String argu) { return n.toString(); }


   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, String argu) {
      return "boolean[]";
   } 

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public String visit(ClassDeclaration n, String argu) {
      String _ret=null;
      String name;
      n.f0.accept(this, argu);
      name = n.f1.accept(this, argu);  //name = n.f1.f0.toString(); also works	 
      n.f2.accept(this, argu);
      n.f3.accept(this, name + " :: ");
      n.f4.accept(this, name + " :: " );
      n.f5.accept(this, argu);
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
   public String visit(MethodDeclaration n, String argu) {
      String _ret=null;
      String funName;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      funName = n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu + funName + " :: ");
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
      n.f11.accept(this, argu);
      n.f12.accept(this, argu);
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
   public String visit(ClassExtendsDeclaration n, String argu) {
      String _ret=null;
      String name;
      n.f0.accept(this, argu);
      name = n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, name + " :: ");
      n.f6.accept(this, name + " :: ");
      n.f7.accept(this, argu);
      return _ret;
   }
}