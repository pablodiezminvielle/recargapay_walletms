# RecargaPay [wallet-ms] 
Wallet Service Assignment

## Prerequisites
- Java 23 or higher
- Maven
- Git

## Run Service
From the `recargapay-dev` directory, run the following commands:

1) ./mvnw clean package
2) java -jar target/wallet-1.0.0.jar

## Features
- **Create Wallet**: Allows users to create wallets and associate them with user accounts.
- **Get Wallet Balance**: Retrieves the current balance of a wallet.
- **Get Historical Wallet Balance**: Retrieves the balance of a wallet as of a specific timestamp based on transaction history.
- **Deposit**: Allows funds to be deposited into a wallet.
- **Withdraw**: Allows funds to be withdrawn from a wallet.
- **Transfer**: Facilitates transferring funds between two wallets.

## Technical Considerations
- **Transaction Logging**: Each wallet operation (`create`, `deposit`, `withdraw`, `transfer`) generates a record in the `Transaction` table to ensure full traceability of all operations. This facilitates auditing of wallet balances, providing a comprehensive log of changes that can be reviewed and analyzed if needed.
- **Transaction Integrity**: Used the `@Transactional` annotation for wallet operations (`create`, `deposit`, `withdraw`, and `transfer`). This ensures data integrity by wrapping these operations in a single transaction, automatically rolling back any changes if any operation fails.
- **Caching**: Implemented caching for the `walletBalance#walletId` to improve response times and reduce resource consumption for repeated balance queries.
- **EnableJpaAuditing**: Activates Spring's feature for entity auditing, which automatically tracks the creation and modification timestamps of entities. This helps to monitor wallet changes and aids in troubleshooting if an error occurs.
- **Database Optimization**:
    - Created an index on the `email` field in the `USER_RPAY` table to optimize searches.
    - Created an index on the `user_uid` field in the `WALLET` table to improve user-based wallet queries.
    - Created indexes on the `TRANSACTION_RPAY` table to optimize searches by `wallet_id` and `timestamp`.
  

## Database
- **Sample Data Script**: A `data.sql` script is included to create the necessary tables and insert sample data for testing and development.
- Database console : http://localhost:8093/h2-console/login.jsp

## Testing with Postman

To test the API using Postman, you can import the collection included in the project:

1. Open Postman.
2. Go to **File > Import**.
3. Select the `RecargaPayAPI.postman_collection.json` file located in the root directory of the project.

## API DOC (SWAGGER)

http://localhost:8093/swagger-ui/index.html#

## Future Improvements
- The **Security & Authentication** section outlines what is missing in terms of user authentication, role-based access control (RBAC), password hashing, and JWT-based communication.
- **Test Coverage**: The current test coverage is below 80%. It needs to be optimized to ensure a higher level of reliability and confidence in the codebase. Additional unit tests and integration tests should be implemented to improve coverage.