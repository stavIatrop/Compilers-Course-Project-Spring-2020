import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        
        System.out.println("Please type your arithmetic expression:");
        try {
            CalcEvaluator evaluate = new CalcEvaluator(System.in);
            System.out.println(evaluate.eval());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        catch(ParseError err){
            System.err.println(err.getMessage());
        }
        

        
    }
}
