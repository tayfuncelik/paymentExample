package com.moneytransfer.service.player;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.payload.response.TransactionResponse;

import java.util.Optional;

public interface PlayerService {

    Optional<Player> findById(Long playerId);

    TransactionResponse creditByTransaction(TransactionRequest transactionRequest)
            throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException;

    TransactionResponse debitByTransaction(TransactionRequest transactionRequest) throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException;
}
