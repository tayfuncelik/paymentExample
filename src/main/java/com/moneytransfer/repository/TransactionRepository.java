package com.moneytransfer.repository;

import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByPlayer(Player player);

  @Query("select t.transactionId from Transaction t")
  List<Long> getAllIds();

}
