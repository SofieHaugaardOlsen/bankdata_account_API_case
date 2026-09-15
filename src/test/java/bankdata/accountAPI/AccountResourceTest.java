package bankdata.accountAPI;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bankdata.accountAPI.AccountResource.CreateAccountRequest;
import bankdata.accountAPI.AccountResource.TransferRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AccountResourceTest {

    @Inject AccountService accountService;
    private Account a1, a2, b1;

    @BeforeEach 
    void setUp() {
       //setting up some existing accounts for testing 
       accountService.resetAccounts();
       a1 = accountService.createAccount(0, 100);
       a2 = accountService.createAccount(0, 0);
       b1 = accountService.createAccount(1, 150);
    }

    @Test 
    void creatingNewAccountsucceeds() {
        CreateAccountRequest req = new CreateAccountRequest(100);
        given()
        .pathParam("ownerID", 0)
        .contentType(ContentType.JSON)
        .body(req)
        .when().post("/accounts/{ownerID}")
        .then().statusCode(200)
               .body("ownerID", is(0))
               .body("accountID", notNullValue())
               .body("heldFunds", is(100));
    }

    @Test 
    void listingUserAccountssucceedsAndGivesCorrectAccounts() {
        given()
        .pathParam("ownerID", 0)
        .when().get("/accounts/{ownerID}")
        .then().statusCode(200)
               .body("$", hasSize(2)) //two entries
               .body("accountID", hasItem((int) a1.getAccountID()))
               .body("accountID", hasItem((int) a2.getAccountID()));
    }

    @Test 
    void ValidTransferSucceeds() {
        TransferRequest treq = new TransferRequest(b1.getAccountID(), 10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", a1.getAccountID())
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(200);
    }

    //Negative tests - verifying error responses
    @Test
    void transferFromNonExistingAccountFails() {
        int nonExistingAccId = 500;
        TransferRequest treq = new TransferRequest(a1.getAccountID(), 10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", nonExistingAccId)
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(404);
    }

    @Test
    void transferToNonExistingAccountFails() {
        int nonExistingAccId = 500;
        TransferRequest treq = new TransferRequest(nonExistingAccId, 10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", a1.getAccountID())
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(404);
    }

    @Test
    void transferFromUnownedAccountfails() {
        TransferRequest treq = new TransferRequest(a1.getAccountID(), 10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", b1.getAccountID())
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(403);
    }

    @Test
    void transferNegativeValueFails() {
        TransferRequest treq = new TransferRequest(b1.getAccountID(), -10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", a1.getAccountID())
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(400);
    }

    @Test
    void transferOverHeldFundsFails() {
        TransferRequest treq = new TransferRequest(b1.getAccountID(), 10);
        given()
        .pathParams("ownerID", 0,
                    "accFrom", a2.getAccountID())
        .contentType(ContentType.JSON)
        .body(treq)
        .when().post("/accounts/{ownerID}/transfer/{accFrom}")
        .then().statusCode(409);
    }

}