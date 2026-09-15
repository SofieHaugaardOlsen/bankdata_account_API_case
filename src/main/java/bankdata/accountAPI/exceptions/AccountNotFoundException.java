package bankdata.accountAPI.exceptions;

public class AccountNotFoundException extends AccountApiException {
    
    public AccountNotFoundException(long accountId, String prefix) {
        super(prefix + "Account not found: " + accountId, 404);
    }
}
