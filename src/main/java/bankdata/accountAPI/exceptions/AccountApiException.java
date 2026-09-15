package bankdata.accountAPI.exceptions;

/**
 * AccountApiException - template class for domain specific exceptions, wraps a message and error code
 * 
 */
public abstract class AccountApiException extends RuntimeException{
    
    private int status;

    public  AccountApiException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
