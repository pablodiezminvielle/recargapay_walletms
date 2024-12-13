package com.recargaypay.wallet.service.impl;

import com.recargaypay.wallet.exception.NoFundsException;
import com.recargaypay.wallet.exception.UserNotFoundException;
import com.recargaypay.wallet.exception.WalletNotFoundException;
import com.recargaypay.wallet.mapper.WalletResponseMapper;
import com.recargaypay.wallet.model.dto.CreateWalletResponse;
import com.recargaypay.wallet.model.dto.WalletTransactionResponse;
import com.recargaypay.wallet.model.entity.Transaction;
import com.recargaypay.wallet.model.entity.TransactionType;
import com.recargaypay.wallet.model.entity.User;
import com.recargaypay.wallet.model.entity.Wallet;
import com.recargaypay.wallet.repository.TransactionRepository;
import com.recargaypay.wallet.repository.UserRepository;
import com.recargaypay.wallet.repository.WalletRepository;
import com.recargaypay.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    public static final String WALLET_NOT_FOUND_FORMAT = "Wallet %s not found";
    public static final String USER_NOT_FOUND_FORMAT = "User %s not found";
    public static final String NO_FUNDS_FORMAT = "Insufficient funds in wallet: %s";

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;


    public WalletServiceImpl(WalletRepository walletRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public CreateWalletResponse createWallet(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_FORMAT, userId)));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        Wallet savedWallet = walletRepository.save(wallet);

        return WalletResponseMapper.toWalletDto(savedWallet);
    }

    @Override
    @Cacheable(value = "walletBalance", key = "#walletUUID")
    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = findWalletById(walletId);
        return wallet.getBalance();
    }

    @Override
    public BigDecimal getHistoricalBalance(UUID walletId, LocalDateTime timestamp) {
        List<Transaction> transactions = transactionRepository.findByWalletIdAndTimestampBefore(walletId, timestamp);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    @CacheEvict(value = "walletBalance", key = "#walletUUID")
    public WalletTransactionResponse deposit(UUID walletId, BigDecimal amount) {
        Wallet wallet = findWalletById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));

        Transaction transaction = createTransaction(wallet, amount, TransactionType.DEPOSIT);
        Wallet savedWallet = walletRepository.save(wallet);

        return WalletResponseMapper.toWalletTransactionResponse(savedWallet);
    }

    @Override
    @Transactional
    @CacheEvict(value = "walletBalance", key = "#walletUUID")
    public WalletTransactionResponse withdraw(UUID walletId, BigDecimal amount) {
        Wallet wallet = findWalletById(walletId);

        // Ensure wallet has sufficient funds
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new NoFundsException(String.format(NO_FUNDS_FORMAT, walletId));
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        Transaction transaction = createTransaction(wallet, amount.negate(), TransactionType.WITHDRAWAL);

        Wallet savedWallet = walletRepository.save(wallet);

        return WalletResponseMapper.toWalletTransactionResponse(savedWallet);
    }

    @Override
    @Transactional
    public WalletTransactionResponse transfer(UUID sourceWalletId, UUID destinationWalletId, BigDecimal amount) {
        Wallet sourceWallet = findWalletById(sourceWalletId);
        Wallet destinationWallet = findWalletById(destinationWalletId);

        // Ensure the source wallet has enough funds
        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new NoFundsException(String.format(NO_FUNDS_FORMAT, sourceWalletId));
        }

        // Deduct the amount from source wallet
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));

        // Add the amount to the destination wallet
        destinationWallet.setBalance(destinationWallet.getBalance().add(amount));

        // Create transactions for both wallets --withdraw from source and deposit to destination --
        Transaction withdrawalTransaction = createTransaction(sourceWallet, amount.negate(), TransactionType.TRANSFER);
        Transaction depositTransaction = createTransaction(destinationWallet, amount, TransactionType.DEPOSIT);

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        // Return the updated destination wallet details
        return WalletResponseMapper.toWalletTransactionResponse(destinationWallet);
    }

    /**
     * Find wallet by its ID, throws exception if not found.
     *
     * @param walletId ID of the wallet to find
     * @return Wallet entity
     */
    private Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(String.format(WALLET_NOT_FOUND_FORMAT, walletId)));
    }

    /**
     * Creates and save the transaction for deposit or withdrawal.
     *
     * @param wallet Wallet involved in the transaction
     * @param amount Transaction amount (positive for deposit, negative for withdrawal)
     * @param type   Type of transaction (deposit or withdrawal)
     * @return Saved transaction data
     */
    private Transaction createTransaction(Wallet wallet, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(type);

        return transactionRepository.save(transaction);
    }
}
