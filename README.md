# recargapay_walletms
Wallet Service Assignment

## Prerequisites
Java 23 or higher Maven Git

## Technical Comments
- Using the @Transactional annotation to Wallet's creatíon to ensure data integrity when creating or updating customer wallets, as it guarantees that all database operations are executed within a single transaction, rolling back changes if any operation fails.
- Added cache to GET Wallet balance service to improve time response (and resources consumption) for repeated operations. Note: there is no specific cache policy implemented due to time restrictions
- 
- 
- 
- Created index on the users table (e.g., on the email field)
- Created index on the wallet table for optimizing searches by user_uid
- Created indexes on transaction table to improve searches by wallet id and timestamp