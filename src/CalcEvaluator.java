import javax.swing.*;
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
        int restExpPart = RestExp(termPart);
        return termPart + restExpPart;
    }

    private int Term(int arg) throws ParseError, IOException {

        if ((lookahead < '0' || lookahead > '9') && lookahead != '(') {
            throw  new ParseError();
        }
        //lookahead is a digit or '('
        int parPart = Par(lookahead);
        int restTermPart = RestTerm(parPart);  //if restTermPart returns -1, null production is chosen
        return restTermPart;
    }

    private int RestExp(int arg) throws ParseError, IOException {

        if (lookahead == ')' || lookahead == -1 || lookahead == '\n') { //null production
            return 0;
        }
        if (lookahead == '+') {
            consume('+');
            int termPart = Term(lookahead);
            int restExpPart = RestExp(termPart);
            return  termPart + restExpPart;
        }
        if (lookahead == '-') {
            consume('-');
            int termPart = Term(lookahead);
            int restExpPart = RestExp(termPart);

//            System.out.println(arg);
//            System.out.println(termPart);
//            System.out.println(restExpPart);
            return -termPart + restExpPart;
        }

        throw new ParseError();
    }

    private int RestTerm(int arg) throws ParseError, IOException {

        if (lookahead == ')' || lookahead == '+' || lookahead == '-' || lookahead == -1 || lookahead == '\n' ) {
            return arg;
        }
        if (lookahead == '*') {
            consume('*');
            int parPart = Par(lookahead);
            parPart = arg * parPart;
            int restTermPart = RestTerm(parPart);
            return restTermPart;
        }
        if (lookahead == '/') {
            consume('/');
            int parPart = Par(lookahead);
            parPart = arg / parPart;
            int restTermPart = RestTerm(parPart);
            return restTermPart;
        }

        throw new ParseError();
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
        int restNumPart = RestNum(digitPart);     //if restNum returns -1, it means that null production is chosen
        return restNumPart;
//        String digitStr = Integer.toString(digitPart);
//        if (restNumPart >= 0) {
//
//            String restNumStr = Integer.toString(restNumPart);
//            digitStr = digitStr + restNumStr;
//        }
//        int finalNum = Integer.parseInt(digitStr);
//        return finalNum;
    }
    private int RestNum(int arg) throws ParseError, IOException {

        if(lookahead == ')' || lookahead == '+' || lookahead == '-' || lookahead == '*'
                || lookahead == '/' || lookahead == -1 || lookahead == '\n') {  //null production
            return arg;
        }
        if (lookahead < '0' || lookahead > '9') {        //lookahead not a digit
            throw new ParseError();
        }
        //lookahead is a digit
        int digitPart = Digit(lookahead);
        int newArg = arg * 10 + digitPart;
        int restNumPart = RestNum(newArg);     //if restNum returns -1, it means that null production is chosen
        return restNumPart;
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
