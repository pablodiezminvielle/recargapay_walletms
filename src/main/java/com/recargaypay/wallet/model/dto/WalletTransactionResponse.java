package com.recargaypay.wallet.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WalletTransactionResponse {

    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;

}
