package com.recargaypay.wallet.model.dto;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public class CreateWalletRequest {

    private UUID userUid;
}
