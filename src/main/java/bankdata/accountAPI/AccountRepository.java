package bankdata.accountAPI;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
/** Repository for persisted {@link Account} entities. */
public class AccountRepository implements PanacheRepository<Account>{
    /**
     * Finds every account owned by a user.
     *
     * @param ownerID the owner identifier
     * @return a list of the user's accounts
     */
    public List<Account> findByOwnerID(long ownerID) {
        return find("ownerID", ownerID).list();
    }
}
