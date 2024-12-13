package com.recargaypay.wallet.service;

import com.recargaypay.wallet.model.dto.CreateWalletResponse;
import com.recargaypay.wallet.model.dto.WalletTransactionResponse;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface WalletService {
    /**
     * @param userId UUID
     * @return created walletId and userId
     */
    @Transactional
    CreateWalletResponse createWallet(UUID userId);

    /**
     * @param walletId UUID
     * @return the current balance of a user's wallet
     */
    BigDecimal getBalance(UUID walletId);

    /**
     * @param walletId  UUID
     * @param timestamp Timestamp
     * @return the balance of a user's wallet at a specific point in the past
     */
    BigDecimal getHistoricalBalance(UUID walletId, LocalDateTime timestamp);

    /**
     * @param walletId UUID
     * @param amount   to deposit
     * @return deposit transaction data
     */
    WalletTransactionResponse deposit(UUID walletId, BigDecimal amount);

    /**
     * @param walletId UUID
     * @param amount to withdraw
     * @return withdraw transaction data
     */
    WalletTransactionResponse withdraw(UUID walletId, BigDecimal amount);

    /**
     * Transfer funds between two wallets.
     *
     * @param sourceWalletId UUID of the source wallet
     * @param destinationWalletId UUID of the destination wallet
     * @param amount The amount to transfer
     * @return WalletTransactionResponse containing the updated details of the destination wallet, including the updated balance and the associated user
     */
    WalletTransactionResponse transfer(UUID sourceWalletId, UUID destinationWalletId, BigDecimal amount);
}
