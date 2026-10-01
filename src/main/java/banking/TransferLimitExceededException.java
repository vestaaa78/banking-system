package banking;

public class TransferLimitExceededException extends RuntimeException {

    public TransferLimitExceededException(String message) {
        super(message);
    }
}
