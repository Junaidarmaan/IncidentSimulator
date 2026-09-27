package com.junnu.incidentsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junnu.incidentsim.entity.Events;
import com.junnu.incidentsim.repository.EventsRepository;
import com.junnu.incidentsim.service.ChatService;

@RestController 
public class ChatController {
    @Autowired 
    ChatService service;
    
    @Autowired
    EventsRepository eventsRepository;
    
    @GetMapping("/chat/{msg}")
    String chat(@PathVariable String msg){
        return service.response(msg);
    }
    
    @GetMapping("/events")
    public Page<Events> getEvents(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return eventsRepository.findAll(pageable);
    }
}
