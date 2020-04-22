import syntaxtree.*;
import visitor.*;
import java.io.*;

public class Main {
    public static void main (String [] args){
    if(args.length != 1){
        System.err.println("Usage: java Main <inputFile>");
        System.exit(1);
    }
    FileInputStream fis = null;
    try{
        fis = new FileInputStream(args[0]);
        MiniJavaParser parser = new MiniJavaParser(fis);
        Goal root = parser.Goal();
        System.err.println("Program parsed successfully.");
        //following block may need change
        ///////////////////////////////////////////////
        SymbolTable sTable = new SymbolTable();
        //visitor that scans the parse tree an creates symbol table
        FirstPhaseVisitor first = new FirstPhaseVisitor();
        try {
            root.accept(first, sTable);

        }
        catch (Exception err) {
            System.out.println("Parse Error: " + err.getMessage());
            return;
        }
        sTable.printSTable();
        sTable.printOffsets();
        SecondPhaseVisitor second = new SecondPhaseVisitor();
        try {
            root.accept(second, sTable);

        }
        catch (Exception err) {
            System.out.println("Parse Error: " + err.getMessage());
            return;
        }
        ///////////////////////////////////////////////
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
