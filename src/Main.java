import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        StringBuilder input1 = new StringBuilder();
        input1.append("stav" );
        input1 = input1.reverse();
        String str1 = input1.toString();
        System.out.println(str1);
        while (true) {
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
}
