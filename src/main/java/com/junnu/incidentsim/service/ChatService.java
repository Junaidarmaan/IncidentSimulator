package com.junnu.incidentsim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junnu.incidentsim.ai.assistants.Assistant;

@Service
public class ChatService {
    @Autowired
    Assistant assistant;

    public String response(String msg) {
        String response = assistant.chat(msg);
        System.out.println("model response is : " + response);
        return response;

    }
}
