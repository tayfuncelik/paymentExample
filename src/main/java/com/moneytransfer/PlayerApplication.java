package com.moneytransfer;

import com.moneytransfer.model.Account;
import com.moneytransfer.model.Player;
import com.moneytransfer.model.Transaction;
import com.moneytransfer.model.TransactionType;
import com.moneytransfer.repository.PlayerRepository;
import com.moneytransfer.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class PlayerApplication {
    private static final Logger logger = LoggerFactory.getLogger(PlayerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PlayerApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(PlayerRepository repository, TransactionRepository transactionRepository) {
        return (args) -> {
            logger.info("....DUMMY DATA INSERTING....");
            Player player = Player.builder()
                    .playerName("TAYFUN")
                    .createdAt(LocalDateTime.now())
                    .build();

            Account account = Account.builder().balance(BigDecimal.valueOf(10)).player(player).build();
            player.setAccount(account);
            repository.save(player);
            Transaction transaction = Transaction.builder()
                    .transactionId(33L)
                    .transactionType(TransactionType.CREDIT)
                    .amount(BigDecimal.valueOf(15))
                    .player(player)
                    .build();
            transactionRepository.save(transaction);
            logger.info("....DUMMY DATA INSERTED....");
        };
    }
}
