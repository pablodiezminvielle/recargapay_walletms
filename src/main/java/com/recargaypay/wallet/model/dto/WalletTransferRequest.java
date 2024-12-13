package com.recargaypay.wallet.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class WalletTransferRequest extends WalletTransactionRequest {
    protected UUID fromWalletId;
}
