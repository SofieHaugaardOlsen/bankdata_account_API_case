package bankdata.accountAPI;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity 
/** A persisted record of a transfer between two accounts. */
public class Transaction extends PanacheEntity{
    private long fromID;
    private long toID;
    private int amount;
    private Instant timestamp;

    protected Transaction() {} //JPA

    /**
     * Creates a transfer record with the current timestamp.
     *
     * @param fromID the source account identifier
     * @param toID the destination account identifier
     * @param amount the transferred amount
     */
    public Transaction(long fromID, long toID, int amount){
        this.fromID = fromID;
        this.toID = toID;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    /** @return the source account identifier */
    public long getFromID() { return fromID;}
    /** @return the destination account identifier */
    public long getToID() { return toID;}
    /** @return the transferred amount */
    public int getAmount() { return amount;}
    /** @return the time at which the transfer record was created */
    public Instant getTimestamp() { return timestamp;}

}
