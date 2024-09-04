package com.banquito.core.account.service;

import com.banquito.core.account.config.RabbitMQConfig;
import com.banquito.core.account.dto.AccountTransactionDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiver {

    private final AccountTransactionService accountTransactionService;

    public RabbitMQReceiver(AccountTransactionService accountTransactionService) {
        this.accountTransactionService = accountTransactionService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveTransaction(AccountTransactionDTO transactionDTO) {
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.println("| Transaccion recibida en la cola: " + transactionDTO + " |");
        System.out.println("\n-------------------------------------------------------------------------------");
        accountTransactionService.processTransaction(transactionDTO);
    }

}
