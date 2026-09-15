package bankdata.accountAPI.exceptions;

public class InvalidAmountException extends AccountApiException{
    public InvalidAmountException(int amount, String prefix) {
        super(prefix + "amount must be positive", 400);
    }
}
