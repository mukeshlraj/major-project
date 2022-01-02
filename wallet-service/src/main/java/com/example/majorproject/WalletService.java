package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private static final String USER_CREATE_TOPIC = "userCreate";
    private static final String TRANSACTION_CREATE_TOPIC = "transactionCreate";
    private static final String WALLET_UPDATE_TOPIC = "walletUpdate";

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = {USER_CREATE_TOPIC}, groupId = "jbdl21")
    public void createWallet(String msg) throws JsonProcessingException {
        JSONObject createWalletRequest = objectMapper.readValue(msg, JSONObject.class);

        String email = (String) createWalletRequest.get("email");
        String phone = (String) createWalletRequest.get("phone");

        Wallet wallet = Wallet.builder()
                .email(email)
                .phone(phone)
                .balance(10.0)
                .build();

        walletRepository.save(wallet);
    }

    @KafkaListener(topics = {TRANSACTION_CREATE_TOPIC}, groupId = "jbdl21")
    public void updateWallet(String msg) throws JsonProcessingException {
        JSONObject updateWalletRequest = objectMapper.readValue(msg, JSONObject.class);

        String transactionId = (String) updateWalletRequest.get("transactionID");
        String sender = (String) updateWalletRequest.get("sender");
        String receiver = (String) updateWalletRequest.get("receiver");
        Double amount = (Double) updateWalletRequest.get("amount");

        Wallet senderWallet = walletRepository.findByEmail(sender);
        Wallet receiverWallet = walletRepository.findByEmail(receiver);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactionID", transactionId);
        jsonObject.put("status", "FAILED");

        if (senderWallet.getBalance() >= amount) {
            walletRepository.updateWallet(receiver, amount);
            walletRepository.updateWallet(sender, 0 - amount);

            jsonObject.put("status", "SUCCESS");
        }
        kafkaTemplate.send(WALLET_UPDATE_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }
}
