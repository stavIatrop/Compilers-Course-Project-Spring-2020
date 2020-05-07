import syntaxtree.*;
import types.*;
import visitor.*;
import my_visitors.*;

import java.io.*;

public class Main {
    public static void main (String[] args){
    
        
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }
        for (String str : args) {
            System.out.println("--------File: " + str + "--------");

            FileInputStream fis = null;
            try{
                fis = new FileInputStream(str);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                
                VTable vTables = new VTable();
                SymbolTable sTable = new SymbolTable();
                //visitor that scans the parse tree an creates symbol table
                FirstPhaseVisitor first = new FirstPhaseVisitor();
                try {
                    root.accept(first, sTable);
                }
                catch (Exception err) {
                    System.out.println("First Visitor: Parse Error: " + err.getMessage());
                    System.out.println();
                    continue;
                }
                //sTable.printSTable();       //just a print method to check that everything is fine with symbol table
                //visitor that does the rest type checking
                SecondPhaseVisitor second = new SecondPhaseVisitor();
                try {
                    root.accept(second, sTable);

                }
                catch (Exception err) {
                    System.out.println("Second Visitor: Parse Error: " + err.getMessage());
                    System.out.println();
                    continue;
                }
                System.err.println("Program parsed successfully.");
                sTable.printStoreOffsets(vTables);      //method to compute and print the offsets

                LLVMIRVisitor llvmirVisitor = new LLVMIRVisitor(sTable, vTables);
                try { 
                    root.accept(llvmirVisitor, null);
                }catch (Exception err){
                    System.out.println("LLVM Visitor: Parse Error: " + err.getMessage());
                    System.out.println();
                    continue;

                }
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                if(fis != null) fis.close();
                }
                catch(IOException ex){
                System.err.println(ex.getMessage());
                }
            }
        }
        
    }
}
