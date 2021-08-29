package com.moneytransfer.service.player;

import com.moneytransfer.exceptions.NonUniqueTransactionException;
import com.moneytransfer.exceptions.ValidationException;
import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.model.TransactionType;
import com.moneytransfer.payload.request.TransactionRequest;
import com.moneytransfer.payload.response.TransactionResponse;
import com.moneytransfer.repository.PlayerRepository;
import com.moneytransfer.service.transaction.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

import static com.moneytransfer.constant.Constant.*;

@Slf4j
@AllArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TransactionService transactionService;

    @Override
    public Optional<Player> findById(final Long playerId) {
        return playerRepository.findById(playerId);
    }

    @Transactional
    @Override
    public synchronized TransactionResponse creditByTransaction(final TransactionRequest transactionRequest) throws ValidationException.NullException, NonUniqueTransactionException, ValidationException.InsufficientBalanceException {
        validateTransaction(transactionRequest.getTransactionId());
        validateLimit(transactionRequest);

        Player player = null;
        try {
            player = playerRepository.findById(transactionRequest.getPlayerId()).get();
        } catch (Exception e) {
            throw new ValidationException.NullException(PLAYER_NOT_FOUND);
        }

        final BigDecimal balance = player.getAccount().getBalance();
        final BigDecimal credit = transactionRequest.getAmount();

        final Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.CREDIT)
                .transactionId(transactionRequest.getTransactionId())
                .player(player)
                .amount(credit).build();

        final BigDecimal newBalance = balance.add(credit);//CREDIT
        player.getAccount().setBalance(newBalance);

        transactionService.save(transaction);
        return TransactionResponse.builder().transactionId(transaction.getTransactionId()).message(TRANSACTION_SUCCESS).build();
    }

    @Transactional
    @Override
    public synchronized TransactionResponse debitByTransaction(final TransactionRequest transactionRequest) throws ValidationException.NullException, ValidationException.InsufficientBalanceException, NonUniqueTransactionException {
        validateTransaction(transactionRequest.getTransactionId());
        validateLimit(transactionRequest);

        Player player = null;
        try {
            player = playerRepository.findById(transactionRequest.getPlayerId()).get();
        } catch (Exception e) {
            throw new ValidationException.NullException(PLAYER_NOT_FOUND);
        }

        final BigDecimal balance = player.getAccount().getBalance();
        final BigDecimal debit = transactionRequest.getAmount();
        final Transaction transaction = Transaction.builder()
                .transactionId(transactionRequest.getTransactionId())
                .transactionType(TransactionType.DEBIT)
                .player(player)
                .amount(debit).build();

        if (balance.compareTo(debit) == -1)
            throw new ValidationException.InsufficientBalanceException(INSUFFICIENT_AMOUNT);

        final BigDecimal newBalance = balance.subtract(debit);//DEBIT
        player.getAccount().setBalance(newBalance);

        transactionService.save(transaction);
        return TransactionResponse.builder().transactionId(transaction.getTransactionId()).message(TRANSACTION_SUCCESS).build();
    }

    private synchronized void validateTransaction(final Long transactionId) throws NonUniqueTransactionException {
        if (transactionService.findById(transactionId) != null) {
            log.error(NON_UNIQUE_TRANSACTION);
            throw new NonUniqueTransactionException(NON_UNIQUE_TRANSACTION);
        }
    }

    private synchronized void validateLimit(final TransactionRequest transactionRequest) throws ValidationException.NullException, ValidationException.InsufficientBalanceException {
        if (transactionRequest == null || (transactionRequest != null && transactionRequest.getAmount() == null))
            throw new ValidationException.NullException(NO_DATA_FOUND);

        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException.InsufficientBalanceException(INVALID_AMOUNT);
    }
}
