package exceptions;

public class PlayerDoesNotExistException extends RuntimeException {
    public PlayerDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
