package com.junnu.incidentsim.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.junnu.incidentsim.service.IncidentService;

@RestController
@RequestMapping("/events")
public class EventsStreamController {

    private final IncidentService incidentService;

    public EventsStreamController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        return incidentService.streamEvents();
    }
}