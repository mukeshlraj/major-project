package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    UserRepository userRepository;

    private static final String USER_CREATE_TOPIC = "userCreate";

    public User getUserByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    public void createUser(User user) throws JsonProcessingException {
        userRepository.save(user);

        JSONObject userRequest = new JSONObject();
        userRequest.put("email", user.getEmail());
        userRequest.put("phone", user.getPhone());

        kafkaTemplate.send(USER_CREATE_TOPIC, objectMapper.writeValueAsString(userRequest));
    }
}
