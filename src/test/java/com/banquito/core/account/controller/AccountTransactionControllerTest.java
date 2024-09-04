package com.banquito.core.account.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.service.AccountTransactionService;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;

@WebMvcTest(AccountTransactionController.class)
public class AccountTransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountTransactionService transactionService;

    @MockBean
    private AccountTransactionMapper transactionMapper;

        private AccountTransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = AccountTransactionDTO.builder()
                .accountId(1)
                .codeChannel("ATM")
                .amount(new BigDecimal("100.00"))
                .debitorAccount("123456")
                .creditorAccount("654321")
                .transactionType("DEB")
                .reference("Ref123")
                .comission(new BigDecimal("10.00"))
                .createDate(LocalDateTime.now())
                .status("EXE")
                .pendiente(new BigDecimal("0.00"))
                .build();
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        given(transactionService.processTransaction(any(AccountTransactionDTO.class))).willReturn(transactionDTO);

        mockMvc.perform(post("/account-microservice/api/v1/account-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\": 1, \"codeChannel\": \"ATM\", \"amount\": 100.00, " +
                        "\"debitorAccount\": \"123456\", \"creditorAccount\": \"654321\", " +
                        "\"transactionType\": \"DEB\", \"reference\": \"Ref123\", \"comission\": 10.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.codeChannel").value("ATM"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.debitorAccount").value("123456"))
                .andExpect(jsonPath("$.creditorAccount").value("654321"))
                .andExpect(jsonPath("$.transactionType").value("DEB"))
                .andExpect(jsonPath("$.reference").value("Ref123"))
                .andExpect(jsonPath("$.comission").value(10.00));
    }

    @Test
    void getTransactionsByCodeUniqueAccount_ShouldReturnTransactionList() throws Exception {
        List<AccountTransaction> transactions = Arrays.asList(
                new AccountTransaction(), new AccountTransaction()
        );
        List<AccountTransactionDTO> transactionDTOs = Arrays.asList(transactionDTO, transactionDTO);

        given(transactionService.findTransactionsByCodeUniqueAccount("123456")).willReturn(transactions);
        given(transactionMapper.toDTOList(transactions)).willReturn(transactionDTOs);

        mockMvc.perform(get("/account-microservice/api/v1/account-transactions/123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].codeChannel").value("ATM"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].debitorAccount").value("123456"))
                .andExpect(jsonPath("$[0].creditorAccount").value("654321"))
                .andExpect(jsonPath("$[0].transactionType").value("DEB"))
                .andExpect(jsonPath("$[0].reference").value("Ref123"))
                .andExpect(jsonPath("$[0].comission").value(10.00));
    }
}

