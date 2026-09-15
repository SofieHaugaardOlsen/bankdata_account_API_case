package bankdata.accountAPI.exceptions;

public class InvalidAccessException extends AccountApiException {

        public InvalidAccessException(long accountId, long userID, String prefix) {
        super(prefix + "User " + userID + " cannot access account " + accountId, 403);
    }
    
}
