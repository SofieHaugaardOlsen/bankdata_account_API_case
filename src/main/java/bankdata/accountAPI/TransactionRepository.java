package bankdata.accountAPI;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
/** Repository for persisted {@link Transaction} entities. */
public class TransactionRepository implements PanacheRepository<Transaction> {

    /**
     * Finds transfers involving an account as either source or destination.
     *
     * @param accID the account identifier
     * @return matching transfer records
     */
    public List<Transaction> findByAccountID(long accID) {
        return list("fromID = ?1 or toID = ?1", accID);
    }

    /**
     * Finds transfers sent from an account.
     *
     * @param fromID the source account identifier
     * @return matching transfer records
     */
    public List<Transaction> findByFromID(long fromID) {
        return list("fromID", fromID);
    }

    /**
     * Finds transfers received by an account.
     *
     * @param toID the destination account identifier
     * @return matching transfer records
     */
    public List<Transaction> findByToID(long toID) {
        return list("toID", toID);
    }
}
