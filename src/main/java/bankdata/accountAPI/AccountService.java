package bankdata.accountAPI;
import bankdata.accountAPI.exceptions.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.util.Collection;
import java.util.List;

import org.jboss.logging.Logger;

@ApplicationScoped
/** Coordinates account creation, lookup, and transfers. */
public class AccountService {
    //keeps track of accounts

    @Inject AccountRepository accountRepo;
    @Inject TransactionRepository transactionRepo;
    private static final Logger log = Logger.getLogger(AccountService.class);

    public AccountService() {
        
    }

    /** @return all accounts currently stored in the repository */
    public Collection<Account> getAllAccounts() {
        return accountRepo.listAll();
    }

    @Transactional
    /** Deletes all stored accounts. Primarily intended for test data reset. */
    public void resetAccounts(){
        accountRepo.deleteAll();
    }

    //creates new account for user with fresh ID
    @Transactional
    /**
     * Creates and persists an account.
     *
     * @param ownerID the account owner identifier
     * @param initialAmount the initial balance
     * @return the newly persisted account
     * @throws InvalidAmountException if the initial amount is negative
     */
    public Account createAccount(long ownerID, int initialAmount) {
        if(initialAmount < 0) {throw new InvalidAmountException(initialAmount, "Account creation failed: ");}
        Account acc = new Account(initialAmount, ownerID);
        accountRepo.persist(acc);
        log.info(String.format("user %d created new account %d holding %d", ownerID, acc.getAccountID(), initialAmount));
        return acc;
    }

    /**
     * Returns all accounts owned by a user.
     *
     * @param userID the owner identifier
     * @return a list of the user's accounts
     */
    public List<Account> getAllUserAccounts(long userID){
        log.info(String.format("listed all accounts of user %d ", userID));
        return accountRepo.findByOwnerID(userID);
    }

    //helper to avoid code duplication
    @Transactional
    protected Account getAccount(long accID){
        Account target = accountRepo.findById((long) accID, LockModeType.PESSIMISTIC_WRITE);
        if (target == null) {throw new AccountNotFoundException(accID, "Transfer failed: ");}
        return target;
    }

    /**
     * Transfers funds from one account to another.
     *
     * @param accFrom the source account identifier
     * @param accTo the destination account identifier
     * @param amount the amount to transfer
     * @param userID the user requesting the transfer
     * @return the persisted transfer record
     * @throws CannotTransferToSelfException if both account identifiers match
     * @throws AccountNotFoundException if either account does not exist
     * @throws InvalidAccessException if the user does not own the source account
     * @throws InvalidAmountException if the amount is not positive
     * @throws NotEnoughFundsException if the source account lacks sufficient funds
     */
    @Transactional
    public Transaction transfer(long accFrom, long accTo, int amount, long userID){
        log.info(String.format("transfer of %d from account %d to account %d ", amount, accFrom, accTo));
        if (accFrom == accTo) {throw new CannotTransferToSelfException("Transfer failed: ");}
        Account source = getAccount(accFrom);
        Account target = getAccount(accTo);

        //only source needs to be owned by user -> such that you can transfer to other peoples accounts
        if (source.getOwnerID() != userID) {throw new InvalidAccessException(accFrom, userID, "Transfer failed: ");}

        Transaction t = new Transaction(accFrom, accTo, amount);
        transactionRepo.persist(t);

        source.withdraw(amount);
        target.deposit(amount);
        log.info(String.format("transfer %d succeeded", t.id));
        return t;
    }

    /**
     * Returns the transaction history for an account owned by a user.
     *
     * @param ownerID the requesting owner's identifier
     * @param accID the account identifier
     * @return a list of transfers involving the account
     * @throws InvalidAccessException if the user does not own the account
     * @throws AccountNotFoundException if account does not exist
     */
    public List<Transaction> getTransactionHistory(long ownerID, long accID) {
        Account target = getAccount(accID);
        if(target.getOwnerID() != ownerID) {throw new InvalidAccessException(accID, ownerID, "Transaction check failed: ");}
        return transactionRepo.findByAccountID(accID);
    }
}
