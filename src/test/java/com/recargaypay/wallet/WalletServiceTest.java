package com.recargaypay.wallet;

import com.recargaypay.wallet.exception.NoFundsException;
import com.recargaypay.wallet.exception.UserNotFoundException;
import com.recargaypay.wallet.exception.WalletNotFoundException;
import com.recargaypay.wallet.model.dto.CreateWalletResponse;
import com.recargaypay.wallet.model.dto.WalletTransactionResponse;
import com.recargaypay.wallet.model.entity.User;
import com.recargaypay.wallet.model.entity.Wallet;
import com.recargaypay.wallet.repository.TransactionRepository;
import com.recargaypay.wallet.repository.UserRepository;
import com.recargaypay.wallet.repository.WalletRepository;
import com.recargaypay.wallet.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletServiceTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        walletService = new WalletServiceImpl(walletRepository, userRepository, transactionRepository);
    }

    @Test
    void testCreateWallet_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.createWallet(userId));
    }

    @Test
    void testCreateWallet_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        CreateWalletResponse response = walletService.createWallet(userId);

        assertNotNull(response);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testGetBalance_WalletNotFound() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.getBalance(walletId));
    }

    @Test
    void testDeposit_Success() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        User user = new User();
        user.setId(UUID.randomUUID());
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(100));

        BigDecimal depositAmount = BigDecimal.valueOf(50);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletTransactionResponse response = walletService.deposit(walletId, depositAmount);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(50));

        BigDecimal withdrawAmount = BigDecimal.valueOf(100);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(NoFundsException.class, () -> walletService.withdraw(walletId, withdrawAmount));
    }

    @Test
    void testWithdraw_Success() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(100));
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        wallet.setUser(user);

        BigDecimal withdrawAmount = BigDecimal.valueOf(50);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletTransactionResponse response = walletService.withdraw(walletId, withdrawAmount);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testTransfer_InsufficientFunds() {
        UUID sourceWalletId = UUID.randomUUID();
        UUID destinationWalletId = UUID.randomUUID();
        Wallet sourceWallet = new Wallet();
        sourceWallet.setId(sourceWalletId);
        sourceWallet.setBalance(BigDecimal.valueOf(50));

        Wallet destinationWallet = new Wallet();
        destinationWallet.setId(destinationWalletId);
        destinationWallet.setBalance(BigDecimal.valueOf(100));

        BigDecimal transferAmount = BigDecimal.valueOf(100);

        when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

        assertThrows(NoFundsException.class, () -> walletService.transfer(sourceWalletId, destinationWalletId, transferAmount));
    }

    @Test
    void testTransfer_Success() {
        UUID sourceWalletId = UUID.randomUUID();
        UUID destinationWalletId = UUID.randomUUID();
        Wallet sourceWallet = new Wallet();
        sourceWallet.setId(sourceWalletId);
        sourceWallet.setBalance(BigDecimal.valueOf(100));

        Wallet destinationWallet = new Wallet();
        destinationWallet.setId(destinationWalletId);
        destinationWallet.setBalance(BigDecimal.valueOf(100));
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        destinationWallet.setUser(user);

        BigDecimal transferAmount = BigDecimal.valueOf(50);

        when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(sourceWallet).thenReturn(destinationWallet);

        WalletTransactionResponse response = walletService.transfer(sourceWalletId, destinationWalletId, transferAmount);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(150), destinationWallet.getBalance());
        assertEquals(BigDecimal.valueOf(50), sourceWallet.getBalance());
        verify(walletRepository, times(2)).save(any(Wallet.class));
    }

    @Test
    void testGetHistoricalBalance_NoTransactions() {
        UUID walletId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();
        when(transactionRepository.findByWalletIdAndTimestampBefore(walletId, timestamp)).thenReturn(List.of());

        BigDecimal historicalBalance = walletService.getHistoricalBalance(walletId, timestamp);

        assertEquals(BigDecimal.ZERO, historicalBalance);
    }
}
