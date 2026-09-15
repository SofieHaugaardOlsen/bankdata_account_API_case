package bankdata.accountAPI.exceptions;

public class CannotTransferToSelfException extends AccountApiException {
    public CannotTransferToSelfException(String prefix) {
        super(prefix + "Accounts cannot transfer to themselves ",  400);
    }
}
