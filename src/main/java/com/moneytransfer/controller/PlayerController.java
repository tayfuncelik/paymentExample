package com.moneytransfer.controller;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.service.player.PlayerService;
import com.moneytransfer.service.transaction.TransactionService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api("Player")
@AllArgsConstructor
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
    @PostMapping("/credit")
    public ResponseEntity<?> creditByTransaction(@RequestBody final TransactionRequest transactionRequest) {
        if (transactionRequest == null)
            return new ResponseEntity<>("Request is empty", HttpStatus.BAD_REQUEST);

        try {
            return new ResponseEntity<>(playerService.creditByTransaction(transactionRequest), HttpStatus.OK);
        } catch (NonUniqueTransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (TransactionException e) {
            log.error("Exception while transaction execution:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Exception while feting transactions:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debitByTransaction(@RequestBody final TransactionRequest transactionRequest) {
        if (transactionRequest == null)
            return new ResponseEntity<>("Request is empty", HttpStatus.BAD_REQUEST);

        try {
            return new ResponseEntity<>(playerService.debitByTransaction(transactionRequest), HttpStatus.OK);
        } catch (NonUniqueTransactionException | ValidationException.InsufficientBalanceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (TransactionException e) {
            log.error("Exception while transaction execution:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Exception while feting transactions:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transaction-history/{playerId}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable final Long playerId) {
        try {
            Player player = playerService.findById(playerId)
                    .orElseThrow(() -> new TransactionException("Player not found"));
            List<Transaction> transactions = transactionService.findByPlayer(player);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Find error:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
