package exceptions;

public class NoSuchCardInHandException extends RuntimeException {
    public NoSuchCardInHandException(String m) {
        super(m);
    }
}