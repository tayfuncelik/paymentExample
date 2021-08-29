package com.moneytransfer.service.transaction;

import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> findByPlayer(Player player);

    Transaction findById(Long id);

    void save(Transaction transaction);

    List<Long> findAllTransactionIdList();
}
