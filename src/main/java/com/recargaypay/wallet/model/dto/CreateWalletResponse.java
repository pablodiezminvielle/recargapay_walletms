package com.recargaypay.wallet.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateWalletResponse {

    private UUID walletId;
    private UUID userId;
}