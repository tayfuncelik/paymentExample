package com.moneytransfer.service.player;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.payload.response.TransactionResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerService {

    Optional<Player> findById(Long playerId);

    CompletableFuture<TransactionResponse> creditByTransaction(TransactionRequest transactionRequest)
            throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException;

    CompletableFuture<TransactionResponse> debitByTransaction(TransactionRequest transactionRequest)
            throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException;
}
