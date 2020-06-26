import java_cup.runtime.*;
import java.io.*;

class Main2 {
    public static void main(String[] argv) throws Exception{
        System.out.println("Please type your expression:");
        Parser p = new Parser(new Scanner(new InputStreamReader(System.in)));
        p.parse();
    }
}
