import java.io.IOException;
import java.io.InputStream;

public class CalcEvaluator {

    private InputStream in;         //read input from terminal
    private int lookahead;

    public CalcEvaluator (InputStream in ) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws ParseError, IOException {

        if (lookahead != symbol)
            throw new ParseError();
        lookahead = in.read();
    }

    private int evalDigit(int digit) { return digit - '0'; }

    public int eval() throws IOException, ParseError {
        int rv = Exp();
        if (lookahead != '\n' && lookahead != -1)
            throw new ParseError();
        return rv;
    }

    private int Exp() throws ParseError, IOException {

        if ((lookahead < '0' || lookahead > '9') && lookahead != '(')
            throw new ParseError();
        //lookahead is a digit or '('
        int termPart = Term(lookahead);
        int restExpPart = RestExp(lookahead);

        return termPart + restExpPart;
    }

    private int Term(int arg) throws ParseError, IOException {

        if ((lookahead < '0' || lookahead > '9') && lookahead != '(') {
            throw  new ParseError();
        }
        //lookahead is a digit or '('
        int parPart = Par(lookahead);
        int restTermPart = restTerm(lookahead);

        return 0;       //THIS RETURN SHOULD CHANGE
    }

    private int RestExp(int arg) throws ParseError, IOException {
        

        return 0;
    }

    private int restTerm(int arg) throws ParseError, IOException {

        return 0;
    }
    private int Par(int arg) throws ParseError, IOException {

        if ((lookahead < '0' || lookahead > '9') && lookahead != '(') {
            throw new ParseError();
        }
        //lookahead is a digit or '('
        if (lookahead >= '0' && lookahead <= '9') {   //lookahead is a digit

            int numPart = Num(lookahead);
            return numPart;
        }
        //lookahead is '('
        consume('(');
        int exp = Exp();
        consume(')');
        return exp;
    }
    private int Num(int arg) throws ParseError, IOException {

        if (lookahead < '0' || lookahead > '9') {       //lookahead not a digit
            throw new ParseError();
        }
        //lookahead is a digit
        int digitPart = Digit(lookahead);
        int restNumPart = RestNum(lookahead);     //if restNum returns -1, it means that null production is chosen
        String digitStr = Integer.toString(digitPart);
        if (restNumPart >= 0) {

            String restNumStr = Integer.toString(restNumPart);
            digitStr = digitStr + restNumStr;
        }
        int finalNum = Integer.parseInt(digitStr);
        return finalNum;
    }
    private int RestNum(int arg) throws ParseError, IOException {

        if(lookahead == ')' || lookahead == '+' || lookahead == '-' || lookahead == '*'
                || lookahead == '/' || lookahead == -1 || lookahead == '\n') {  //null production
            return -1;
        }
        if (lookahead < '0' || lookahead > '9') {        //lookahead not a digit
            throw new ParseError();
        }
        //lookahead is a digit
        int digitPart = Digit(lookahead);
        int restNumPart = RestNum(lookahead);     //if restNum returns -1, it means that null production is chosen
        String digitStr = Integer.toString(digitPart);
        if (restNumPart >= 0) {

            String restNumStr = Integer.toString(restNumPart);
            digitStr = digitStr + restNumStr;
        }
        int finalNum = Integer.parseInt(digitStr);
        return finalNum;

    }
    private int Digit(int arg) throws ParseError, IOException {

        if (lookahead < '0' || lookahead > '9') {       //lookahead not a digit
            throw new ParseError();
        }
        //lookahead is a digit
        int digit = evalDigit(lookahead);
        consume(lookahead);
        return digit;
    }
}
