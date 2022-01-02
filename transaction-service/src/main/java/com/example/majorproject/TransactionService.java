package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    private static final String TRANSACTION_CREATE_TOPIC = "transactionCreate";
    private static final String WALLET_UPDATE_TOPIC = "walletUpdate";

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public String doTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionStatus(TransactionStatus.PENDING)
                .amount(transactionRequest.getAmount())
                .sender(transactionRequest.getSender())
                .receiver(transactionRequest.getReceiver())
                .build();

        transactionRepository.save(transaction);

        JSONObject transactionCreate = new JSONObject();
        transactionCreate.put("transactionID", transaction.getTransactionId());
        transactionCreate.put("sender", transaction.getSender());
        transactionCreate.put("receiver", transaction.getReceiver());
        transactionCreate.put("amount", transaction.getAmount());

        kafkaTemplate.send(TRANSACTION_CREATE_TOPIC, objectMapper.writeValueAsString(transactionCreate));

        return transaction.getTransactionId();
    }

    @KafkaListener(topics = {WALLET_UPDATE_TOPIC}, groupId = "jbdl21")
    public void updateTransaction(String msg) throws JsonProcessingException {
        JSONObject walletUpdate = objectMapper.readValue(msg, JSONObject.class);

        String transactionID = (String) walletUpdate.get("transactionID");
        String status = (String) walletUpdate.get("status");

        transactionRepository.updateTransaction(transactionID, status);
    }
}
