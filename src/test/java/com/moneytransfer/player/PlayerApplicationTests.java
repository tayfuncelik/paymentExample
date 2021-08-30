package com.moneytransfer.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneytransfer.model.TransactionType;
import com.moneytransfer.payload.request.TransactionRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static com.moneytransfer.constant.Constant.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    void contextLoads() {
    }

    @Test
    @Order(2)
    public void getBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/player/balance/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(10));
    }

    @Test
    @Order(3)
    public void creditByTransaction() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(111L);
        transactionRequest.setPlayerId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(20));

        final MvcResult mvcResult = getMvcResultForCredit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(transactionRequest.getTransactionId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(TRANSACTION_SUCCESS));

    }

    @Test
    @Order(4)
    public void debitByTransaction() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(222222L);
        transactionRequest.setPlayerId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(5));

        final MvcResult mvcResult = getMvcResultForDebit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(transactionRequest.getTransactionId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(TRANSACTION_SUCCESS));
    }

    @Test
    @Order(5)
    public void getTransactionHistory() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/player/transaction-history/{playerId}", 1)
                .accept(MediaType.APPLICATION_JSON);

        final MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].transactionType").value(TransactionType.CREDIT.name()))//Default insert
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].transactionId").value(1L))//Default insert
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(15))//Default insert

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].transactionType").value(TransactionType.CREDIT.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].transactionId").value(111L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount").value(20))

                .andExpect(MockMvcResultMatchers.jsonPath("$[2].transactionType").value(TransactionType.DEBIT.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].transactionId").value(222222L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].amount").value(5));
    }

    @Test
    @Order(6)
    public void uniqueTransactionException() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(1L);
        transactionRequest.setPlayerId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(5));

        final MvcResult mvcResult = getMvcResultForDebit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(NON_UNIQUE_TRANSACTION));
    }

    @Test
    @Order(7)
    public void inSufficientBalanceAmount() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(55L);
        transactionRequest.setPlayerId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(555));

        final MvcResult mvcResult = getMvcResultForDebit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(INSUFFICIENT_AMOUNT));
    }

    @Test
    @Order(8)
    public void playerNotFoundDebit() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(55L);
        transactionRequest.setPlayerId(333L);
        transactionRequest.setAmount(BigDecimal.valueOf(555));

        final MvcResult mvcResult = getMvcResultForDebit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(PLAYER_NOT_FOUND));
    }

    @Test
    @Order(9)
    public void playerNotFoundCredit() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(55L);
        transactionRequest.setPlayerId(333L);
        transactionRequest.setAmount(BigDecimal.valueOf(555));

        final MvcResult mvcResult = getMvcResultForCredit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(PLAYER_NOT_FOUND));
    }

    @Test
    @Order(10)
    public void validateLimit() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(55L);
        transactionRequest.setPlayerId(333L);
        transactionRequest.setAmount(null);

        final MvcResult mvcResult = getMvcResultForCredit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(NO_DATA_FOUND));
    }

    @Test
    @Order(10)
    public void validateLimitBalanceCheck() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId(55L);
        transactionRequest.setPlayerId(333L);
        transactionRequest.setAmount(BigDecimal.valueOf(-8));

        final MvcResult mvcResult = getMvcResultForCredit(transactionRequest);
        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(INVALID_AMOUNT));
    }

    @NotNull
    private MvcResult getMvcResultForCredit(TransactionRequest transactionRequest) throws Exception {
        ObjectMapper Obj = new ObjectMapper();
        String content = Obj.writeValueAsString(transactionRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/player/credit")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        return mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();
    }

    @NotNull
    private MvcResult getMvcResultForDebit(TransactionRequest transactionRequest) throws Exception {
        ObjectMapper Obj = new ObjectMapper();
        String content = Obj.writeValueAsString(transactionRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/player/debit")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        return mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();
    }
}
