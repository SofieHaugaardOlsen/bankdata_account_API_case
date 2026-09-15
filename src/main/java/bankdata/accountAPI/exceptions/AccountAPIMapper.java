package bankdata.accountAPI.exceptions;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

/**
 * AccountAPIMapper - maps thrown exceptions to HTTP responses
 */
@Provider
public class AccountAPIMapper implements ExceptionMapper<AccountApiException> {

    private static final Logger log = Logger.getLogger(AccountAPIMapper.class);

    @Override
    public Response toResponse(AccountApiException exception) {

        log.error(exception.getMessage());
        return Response.status(exception.getStatus())
                       .entity(exception.getMessage()).build();
    }
    
}
