package com.recargaypay.wallet.controller;

import com.recargaypay.wallet.model.dto.*;
import com.recargaypay.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    //TODO Check why data.sql data is not being automatically imported

    @PostMapping
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request.getUserUid()));
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{walletId}/balance/historical")
    public ResponseEntity<BigDecimal> getHistoricalBalance(@PathVariable UUID walletId, @RequestParam String timestamp) {
        LocalDateTime time = LocalDateTime.parse(timestamp);
        BigDecimal balance = walletService.getHistoricalBalance(walletId, time);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletTransactionResponse> deposit(@RequestBody WalletTransactionRequest request) {
        WalletTransactionResponse walletDto = walletService.deposit(request.getToWalletId(), request.getAmount());
        return ResponseEntity.ok(walletDto);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletTransactionResponse> withdraw(@RequestBody WalletTransactionRequest request) {
        WalletTransactionResponse walletDto = walletService.withdraw(request.getToWalletId(), request.getAmount());
        return ResponseEntity.ok(walletDto);
    }

    @PostMapping("/transfer")
    public ResponseEntity<WalletTransactionResponse> transfer(@RequestBody WalletTransferRequest request) {
        WalletTransactionResponse walletDto = walletService.transfer(request.getFromWalletId(), request.getToWalletId(), request.getAmount());
        return ResponseEntity.ok(walletDto);
    }
}
