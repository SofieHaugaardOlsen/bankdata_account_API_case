package bankdata.accountAPI;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

/** REST endpoints for account and transfer operations. */
@Path("/accounts")
public class AccountResource {

    @Inject AccountService accountService;
    private static final Logger log = Logger.getLogger(AccountResource.class);

    /** Request body used to create an account. */
    public record CreateAccountRequest(int initialAmount) {}

    /** Request body used to transfer funds. */
    public record TransferRequest(long toID, int amount) {}

    //CREATE
    /** Creates an account for the specified owner. */
    @POST 
    @Path("/{ownerID}")
    @Consumes(MediaType.APPLICATION_JSON)   
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(@PathParam("ownerID") long ownerID, CreateAccountRequest accreq) {
        log.info(String.format("user %d requested creation of new account with funds %d", ownerID, accreq.initialAmount));
        Account newAccount = accountService.createAccount(ownerID, accreq.initialAmount);
        return newAccount;
    }

    //LIST
    /** Lists all accounts owned by the specified user. */
    @GET 
    @Path("/{ownerID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getUserAccounts(@PathParam("ownerID") long ownerID) {
        log.info(String.format("user %d requested list of owned accounts", ownerID));
        return accountService.getAllUserAccounts(ownerID);
    }

    //TRANSFER
    /** Transfers funds from the specified source account. */
    @POST
    @Path("/{ownerID}/transfer/{fromID}")
    @Consumes(MediaType.APPLICATION_JSON)   
    public Response transfer(@PathParam("ownerID") long ownerID, @PathParam("fromID") long fromID, TransferRequest tr) {
        log.info(String.format("user %d requested transfer from=%d, to=%d, amount=%d", ownerID, fromID, tr.toID, tr.amount));
        Transaction t = accountService.transfer(fromID, tr.toID, tr.amount, ownerID); 
        return Response.ok(t).build();
        //note that if transfer fails the exception mapper will build an error response & the transaction will not be saved
    }

    /** Returns the transaction history for an account owned by the user. */
    @GET
    @Path("/{ownerID}/transactions/{accID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransactionHistory(@PathParam("ownerID") long ownerID, @PathParam("accID") long accID) {
        log.info(String.format("user %d requested transaction history of account %d", ownerID, accID));
        return accountService.getTransactionHistory(ownerID, accID);
    }
    
}
