package com.recargaypay.wallet.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFundsException extends RuntimeException {
    public NoFundsException(String message) {
        log.error(message);
    }
}