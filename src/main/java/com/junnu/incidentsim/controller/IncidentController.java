package com.junnu.incidentsim.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junnu.incidentsim.dto.IncidentPlan;
import com.junnu.incidentsim.entity.Events;
import com.junnu.incidentsim.repository.EventsRepository;
import com.junnu.incidentsim.service.IncidentService;
@RestController 
public class IncidentController {

    IncidentService incidentService;
    EventsRepository eventsRepository;

    public  IncidentController(IncidentService incidentService, EventsRepository eventsRepository){
        this.incidentService = incidentService;
        this.eventsRepository = eventsRepository;
    }


    @GetMapping("simulate/{message}")
    IncidentPlan simulateIncidents(@PathVariable String message){
        return incidentService.simulateIncident(message);

    }

    @GetMapping("/incidents/records")
    public Page<Events> getIncidentRecords(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return eventsRepository.findAll(pageable);
    }

}
