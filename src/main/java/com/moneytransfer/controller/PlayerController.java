package com.moneytransfer.controller;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.payload.response.TransactionResponse;
import com.moneytransfer.service.player.PlayerService;
import com.moneytransfer.service.transaction.TransactionService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Api("Player")
@AllArgsConstructor
//@NoArgsConstructor
@Slf4j
@RequestMapping("/api/player")
@RestController
public class PlayerController {

    private final PlayerService playerService;
    private final TransactionService transactionService;

    @GetMapping("/balance/{playerId}")
    public ResponseEntity<?> getBalance(@PathVariable final Long playerId) throws TransactionException {
        try {
            BigDecimal balance = playerService.findById(playerId)
                    .orElseThrow(() -> new TransactionException("Player not found"))
                    .getAccount()
                    .getBalance();
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Find error:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Debit /Withdrawal per player A debit transaction will only succeed if there are sufficient
     * funds on the account (balance - debit amount >= 0).
     */
    @SneakyThrows
    @PostMapping("/credit")
    public @ResponseBody
    CompletableFuture<ResponseEntity> creditByTransaction(@RequestBody final TransactionRequest transactionRequest) throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException {

        CompletableFuture<ResponseEntity> future = new CompletableFuture<>();
        playerService.creditByTransaction(transactionRequest).thenApply(transactionResponse -> new ResponseEntity<>(transactionResponse, HttpStatus.OK))
                .exceptionally(getThrowableResponseEntityFunction()).whenComplete((response, throwable) -> {
                    future.complete((ResponseEntity) response);
                });
        return future;
    }

    @NotNull
    private Function<Throwable, ResponseEntity<TransactionResponse>> getThrowableResponseEntityFunction() {
        return e -> {
            if (e.getCause() instanceof NonUniqueTransactionException)
                return new ResponseEntity(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            if (e.getCause() instanceof ValidationException.InsufficientBalanceException)
                return new ResponseEntity(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            if (e.getCause() instanceof TransactionException)
                return new ResponseEntity(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity(e.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PostMapping("/debit")
    public @ResponseBody
    CompletableFuture<ResponseEntity> debitByTransaction(@RequestBody final TransactionRequest transactionRequest) throws NonUniqueTransactionException, ValidationException.InsufficientBalanceException, ValidationException.NullException {

        CompletableFuture<ResponseEntity> future = new CompletableFuture<>();
        playerService.debitByTransaction(transactionRequest).thenApply(transactionResponse -> new ResponseEntity<>(transactionResponse, HttpStatus.OK))
                .exceptionally(getThrowableResponseEntityFunction()).whenComplete((response, throwable) -> {
                    future.complete((ResponseEntity) response);
                });
        return future;
    }


    @GetMapping("/transaction-history/{playerId}")
    public @ResponseBody
    CompletableFuture<ResponseEntity> getTransactionHistory(@PathVariable final Long playerId) {

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new TransactionException("Player not found"));
        return transactionService.findByPlayer(player).<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleTransactionFailure);
    }

    private static Function<Throwable, ResponseEntity<? extends List<Transaction>>> handleTransactionFailure = throwable -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
}
