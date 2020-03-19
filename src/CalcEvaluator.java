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

        return 0;
    }

    private int RestExp() throws ParseError, IOException {

        return 0;
    }
    private int Term() throws ParseError, IOException {

        return 0;
    }
    private int restTerm() throws ParseError, IOException {

        return 0;
    }
    private int Par() throws ParseError, IOException {

        return 0;
    }
    private int Num() throws ParseError, IOException {

        return 0;
    }
    private int RestNum() throws ParseError, IOException {

        return 0;
    }
    private int Digit() throws ParseError, IOException {

        return 0;
    }
}
