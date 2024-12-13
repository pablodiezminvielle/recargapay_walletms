package com.recargaypay.wallet.mapper;

import com.recargaypay.wallet.model.dto.CreateWalletResponse;
import com.recargaypay.wallet.model.dto.WalletTransactionResponse;
import com.recargaypay.wallet.model.entity.Wallet;

public class WalletResponseMapper {

    public static CreateWalletResponse toWalletDto(Wallet wallet) {
        CreateWalletResponse response = new CreateWalletResponse();
        response.setWalletId(wallet.getId());
        response.setUserId(wallet.getUser().getId());
        return response;
    }

    public static WalletTransactionResponse toWalletTransactionResponse(Wallet wallet) {
        WalletTransactionResponse response = new WalletTransactionResponse();
        response.setId(wallet.getId());
        response.setUserId(wallet.getUser().getId());
        response.setBalance(wallet.getBalance());
        response.setLastUpdated(wallet.getUpdatedAt());
        return response;
    }
}
