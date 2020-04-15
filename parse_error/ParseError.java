package parse_error;

public class ParseError extends Exception {

    String msg;

    public ParseError(String message) {
        msg = message;
    }
    public String getMessage() {
        return msg;
    }
}