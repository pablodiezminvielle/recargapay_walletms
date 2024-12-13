package com.recargaypay.wallet.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletTransactionRequest {
    protected UUID toWalletId;
    protected BigDecimal amount;
}
