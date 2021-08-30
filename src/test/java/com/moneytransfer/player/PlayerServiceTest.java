package com.moneytransfer.player;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.payload.response.TransactionResponse;
import com.moneytransfer.service.player.PlayerService;
import com.moneytransfer.service.transaction.TransactionService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TransactionService transactionService;

    @Test
    public void threadSafeCreditDebitOperation() throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException {

        List<CompletableFuture> futures = new ArrayList<>();
        for (long i = 2; i < 5; i++) {
            CompletableFuture<TransactionResponse> future = playerService.creditByTransaction(generateMockRequest(i));
            futures.add(future);
        }
        for (long i = 5; i <= 7; i++) {
            CompletableFuture<TransactionResponse> future = playerService.debitByTransaction(generateMockRequest(i));
            futures.add(future);
        }

        futures.stream().forEach(f -> CompletableFuture.allOf(f).join());
    }

    @Test
    public void transactionHistory() throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException, ExecutionException, InterruptedException {

        Player player = playerService.findById(1L).get();
        CompletableFuture<List<Transaction>> future = transactionService.findByPlayer(player);
//        Assertions.assertEquals(future.get().size(), 9);
        AtomicInteger counter = new AtomicInteger(0);
        future.get().forEach(transaction -> {
            System.out.println("" + transaction.getTransactionId() + "" + transaction.getTransactionType() + "_" + transaction.getAmount());
//                Assertions.assertEquals(future.get().get(counter.getAndIncrement()).getTransactionId(), transaction.getTransactionId());
        });
    }

    private TransactionRequest generateMockRequest(Long transactionId) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setPlayerId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(20));
        transactionRequest.setTransactionId(transactionId);
        return transactionRequest;
    }
}
