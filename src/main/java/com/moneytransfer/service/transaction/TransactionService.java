package com.moneytransfer.service.transaction;

import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {

    CompletableFuture<List<Transaction>> findByPlayer(Player player);

    Transaction findById(Long id);

    void save(Transaction transaction);

    List<Long> findAllTransactionIdList();
}
