package bankdata.accountAPI;

import bankdata.accountAPI.exceptions.InvalidAmountException;
import bankdata.accountAPI.exceptions.NotEnoughFundsException;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bank account with an owner and a current funds balance.
 *
 * <p>The entity is persisted by Hibernate through {@link PanacheEntity} and
 * is also exposed as JSON by the REST resource.</p>
 */
@Entity 
public class Account extends PanacheEntity{
    
    private int heldFunds;
    private long ownerID;

    protected Account() {} //JPA

    /**
     * Creates an account with an initial balance and owner.
     *
     * @param heldFunds the initial balance
     * @param ownerID the identifier of the account owner
     */
    public Account( int heldFunds, long ownerID) {
        this.heldFunds = heldFunds;
        this.ownerID = ownerID;
    }

    /**
     * Adds funds to this account.
     *
     * @param amount the positive amount to deposit
     * @throws InvalidAmountException if {@code amount} is zero or negative
     */
    public void deposit(int amount) {
        if (amount <= 0) {throw new InvalidAmountException(amount, "Transfer failed: ");}
        heldFunds += amount;
    }

    /**
     * Removes funds from this account.
     *
     * @param amount the positive amount to withdraw
     * @throws InvalidAmountException if {@code amount} is zero or negative
     * @throws NotEnoughFundsException if the account does not have enough funds
     */
    public void withdraw(int amount) {
        if (amount <= 0) {throw new InvalidAmountException(amount, "Transfer failed: ");}
        if (heldFunds < amount) {throw new NotEnoughFundsException(this.id, amount, "Transfer failed: ");}
        heldFunds -= amount;
    }

    /**
     * @return the amount currently held in the account
     */
    @JsonProperty("heldFunds")
    public int getFunds() {return heldFunds;}

    /**
     * @return the owner identifier
     */
     @JsonProperty("ownerID")
    public long getOwnerID() {return ownerID;}

    /**
     * @return the account identifier
     */
     @JsonProperty("accountID")
    public long getAccountID() {return this.id;}
}
