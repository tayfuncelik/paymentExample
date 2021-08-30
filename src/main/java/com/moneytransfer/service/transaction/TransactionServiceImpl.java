package com.moneytransfer.service.transaction;

import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Async
    @Override
    public CompletableFuture<List<Transaction>> findByPlayer(final Player player) {
        return CompletableFuture.completedFuture(transactionRepository.findByPlayer(player));
    }

    @Override
    public Transaction findById(final Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public void save(final Transaction transaction) {
        transactionRepository.save(transaction);
    }

    //TODO this method can be used for caching Id list
    //later on we wouldn't needt send query all the time
    @Override
    public List<Long> findAllTransactionIdList() {
        return transactionRepository.getAllIds();
    }
}
