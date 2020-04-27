import syntaxtree.*;
import visitor.*;
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
                System.err.println("Program parsed successfully.");
                
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
                sTable.printSTable();       //just a print methid to check that everything is fine with symbol table
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
                sTable.printOffsets();      //method to compute and print the offsets
                
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
