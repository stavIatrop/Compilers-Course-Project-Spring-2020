package my_visitors;

import types.*;
import visitor.GJDepthFirst;

public class LLVMIRVisitor extends GJDepthFirst<String, String>{
    
    public SymbolTable sTable;
    public VTable vTables;

    public LLVMIRVisitor(SymbolTable stable, VTable vtables) {
        sTable = stable;
        vTables = vtables;
    }

}