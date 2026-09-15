package bankdata.accountAPI.exceptions;

public class NotEnoughFundsException extends AccountApiException {
    public NotEnoughFundsException(long accID, int amount,String prefix) {
        super(prefix + "account " + accID + " contains less than " + amount, 409);
    }
}
