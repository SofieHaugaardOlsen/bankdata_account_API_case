package bankdata.accountAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import bankdata.accountAPI.Account;
import bankdata.accountAPI.AccountService;
import bankdata.accountAPI.exceptions.AccountNotFoundException;
import bankdata.accountAPI.exceptions.InvalidAccessException;
import bankdata.accountAPI.exceptions.InvalidAmountException;
import bankdata.accountAPI.exceptions.NotEnoughFundsException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;


@QuarkusTest
public class AccountServiceTest {
    
    String transfer_prefix = "Transfer failed:";
    @Inject AccountService accountService;
    private Account a1, a2 ,b1;

    @BeforeEach 
    void setUp() {
        accountService.resetAccounts();
        a1 = accountService.createAccount(0, 100);
        a2 = accountService.createAccount(0, 0);
        b1 = accountService.createAccount(1, 50);
    }

    @Test 
    void creatingAccountUpdatesCollection(){
        int sizeBefore= accountService.getAllAccounts().size();
        accountService.createAccount(2,1000);
        assertEquals(sizeBefore + 1, accountService.getAllAccounts().size());
    }

    @Test
    void getAllUserAccountsReturnsOnlyAccountsForGivenUser() {
        List<Account> res =  accountService.getAllUserAccounts(0);
        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(a->a.getAccountID() == a1.getAccountID()));
        assertTrue(res.stream().anyMatch(a->a.getAccountID() == a2.getAccountID()));
    }   

    @Test
    void cannotTransferFromNonExistingAccount() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class, 
            () -> accountService.transfer(200, a1.getAccountID(), 10, 0));
        assertEquals(String.format("%s Account not found: 200",transfer_prefix), e.getMessage());
    }

    @Test
    void cannotTransferToNonExistingAccount() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class, 
            () -> accountService.transfer(a1.getAccountID(), 200, 10, 0));
        assertEquals(String.format("%s Account not found: 200", transfer_prefix), e.getMessage());
    }

    @Test
    void cannotTransferFromUnownedAccount() {
        InvalidAccessException e = assertThrows(InvalidAccessException.class, 
            () -> accountService.transfer(b1.getAccountID(), a1.getAccountID(), 10, 0));
        assertEquals(String.format("%s User 0 cannot access account %d",transfer_prefix, b1.getAccountID()), e.getMessage());
    }

    @Test
    void cannotTransferNegativeAmount() {
        InvalidAmountException e = assertThrows(InvalidAmountException.class, 
            () -> accountService.transfer(a1.getAccountID(), b1.getAccountID(), -10, 0));
        assertEquals(String.format("%s amount must be positive", transfer_prefix) ,e.getMessage());
    }

    @Test
    void cannotTransferMoreFundsThanAreAvailable() {
         NotEnoughFundsException e = assertThrows( NotEnoughFundsException.class, 
            () -> accountService.transfer(a2.getAccountID(), b1.getAccountID(), 10, 0));
        assertEquals(String.format("%s account %d contains less than 10", transfer_prefix,a2.getAccountID()), e.getMessage());
    }

    @Test
    void validTransferUpdatesAccountsCorrectlyAndCreatesTransaction() {
        int source_val_before = a1.getFunds();
        int target_val_before = b1.getFunds();

        accountService.transfer(a1.getAccountID(), b1.getAccountID(), 50, 0);
        assertEquals(source_val_before - 50, accountService.getAccount(a1.getAccountID()).getFunds());
        assertEquals(target_val_before + 50, accountService.getAccount(b1.getAccountID()).getFunds());
        assertTrue(accountService.getTransactionHistory(0L, a1.getAccountID()).size() == 1);
    }
}
