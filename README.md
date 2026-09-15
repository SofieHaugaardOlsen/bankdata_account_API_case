# Account API

**Author:** Sofie Haugaard Olsen

REST API for creating accounts, listing a user's accounts, transferring
funds, and viewing transaction history.

The implementation includes domain-level error handling, ownership checks,
transaction auditing, application health, and PostgreSQL persistence.

## Setup requirements
- Java 23 or newer
- Docker, for the PostgreSQL database provided by Quarkus Dev Services

The Maven wrapper is included, so Maven does not need to be installed
separately.

## Implementation overview

- **Error handling:** Domain exceptions are mapped to HTTP status codes such
	as `400`, `403`, `404`, and `409`.
- **Security:** The service verifies that the requesting owner controls the
	source account and transaction-history account. Authentication is outside
	the scope of this implementation, so `ownerID` is currently supplied by
	the caller and treated as trusted. In production, it should be derived
	from an authenticated identity rather than a user-controlled
	URL parameter.
- **Auditing:** Successful transfers are persisted as `Transaction` records
	with source, destination, amount, and timestamp fields.
- **Observability:** Quarkus SmallRye Health exposes simple health checks, and
	application logs record account creation, account listing, transfer
	requests, and successful transfers.
- **Persistence:** Hibernate ORM with Panache persists accounts and
	transactions in PostgreSQL. The development profile intentionally uses
	`drop-and-create`, so the schema and data are reset when the dev environment
	restarts. The production profile uses `none`, allowing an externally
	managed database to retain data across deployments.

The application uses Quarkus REST, Jackson, Hibernate ORM with Panache, and
PostgreSQL.



## Run locally

Start the application in development mode:

```powershell
./mvnw.cmd quarkus:dev
```

Quarkus starts the API at `http://localhost:8080`. In development mode,
Hibernate recreates the schema and `import.sql` loads sample accounts and
transactions on startup. This reset is convenient for the case and tests but
means development data is not durable. The log file is written to
`logs/application.log`.

Open the interactive OpenAPI documentation in Swagger UI

```text
http://localhost:8080/q/swagger-ui/
```

The Quarkus Dev UI is available at:

```text
http://localhost:8080/q/dev-ui/
```

## API

The current API uses an owner ID in the URL. Example requests use the seeded
accounts: 
- account `1` and `2` belong to user `22`
- account `3` belongs to user `42`.

### Create an account

```http
POST /accounts/{ownerID}
Content-Type: application/json

{"initialAmount": 500}
```

Returns the created account as JSON. The initial amount must not be negative.

Example, creating an account with an initial balance of 500 for owner 22:

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/accounts/22" `
    -Method Post `
    -ContentType "application/json" `
    -Body '{"initialAmount":500}'
```

### List a user's accounts

```http
GET /accounts/{ownerID}
```

Example:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts/22" -Method Get
```

### Transfer funds

```http
POST /accounts/{ownerID}/transfer/{fromID}
Content-Type: application/json

{"toID": 3, "amount": 100}
```

Example, transferring 100 from account 1 to account 3 as owner 22:

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/accounts/22/transfer/1" `
    -Method Post `
    -ContentType "application/json" `
    -Body '{"toID":2,"amount":50}'
```

The source account must belong to the requesting owner. The destination may
belong to another owner.

### Get transaction history

```http
GET /accounts/{ownerID}/transactions/{accountID}
```

Example:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts/22/transactions/1"  -Method Get
```

## Error responses

Domain errors are mapped to HTTP responses with the exception message as the
response body.

| Situation | Status |
| --- | ---: |
| Invalid or non-positive amount | 400 |
| Transfer to the same account | 400 |
| Account not found | 404 |
| Insufficient funds | 409 |
| User does not own account | 403 |

## Tests

Run the complete test suite directly from a console:

```powershell
.\mvnw.cmd test
```

Tests can also be run while Quarkus is already running in dev mode by pressing r:

The tests cover account creation, account listing, transfers, ownership
checks, missing accounts, invalid amounts, and insufficient funds.

## Optional features

- **Persistence:** Implemented with PostgreSQL and Hibernate ORM with Panache.
	Quarkus Dev Services manages the database locally, and the development
	profile reloads the sample data on startup, i.e. development data is not retained between restarts. For proper persistence, the application should use an externally managed PostgreSQL database and a migration-based schema strategy instead of `drop-and-create`.

- **Frontend:** Not implemented; the API can be explored through Swagger UI.
- **Deployment:** Not deployed to a public cloud; 
