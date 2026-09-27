package com.junnu.incidentsim.ai;

import java.util.Arrays;
import java.util.List;


import com.junnu.incidentsim.entity.Events;
import com.junnu.incidentsim.enums.Component;
import com.junnu.incidentsim.enums.Service;
import com.junnu.incidentsim.enums.Severity;
import com.junnu.incidentsim.repository.EventsRepository;

import dev.langchain4j.agent.tool.Tool;

@org.springframework.stereotype.Component
public class Tools {

    private final EventsRepository eventsRepository;

    public Tools(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    @Tool("Returns all services available for incident generation")
    public List<String> getAvailableServices() {
        return Arrays.stream(Service.values())
                .map(Enum::name)
                .toList();
    }
    @Tool("returns count of no of events/rows in database of all types")
    public Long numberOfEventsInDB(){
        Long count = eventsRepository.count();
        return count;
    }

    @Tool("Returns all components available for incident generation")
    public List<String> getAvailableComponents() {
        return Arrays.stream(Component.values())
                .map(Enum::name)
                .toList();
    }

    @Tool("Returns all severities available for incident generation")
    public List<String> getAvailableSeverities() {
        return Arrays.stream(Severity.values())
                .map(Enum::name)
                .toList();
    }

    @Tool("Fetches all events for a given severity. Example values: LOW, MEDIUM, HIGH, CRITICAL")
    public List<Events> getEventsBySeverity(String severity) {
        return eventsRepository.findBySeverity(parseSeverity(severity));
    }

    @Tool("Fetches all events for a given component. Example values: DATABASE, CACHE, API, NETWORK")
    public List<Events> getEventsByComponent(String component) {
        return eventsRepository.findByComponent(parseComponent(component));
    }

    @Tool("Fetches all events for a given service. Example values: PAYMENT_SERVICE, ORDER_SERVICE, AUTH_SERVICE, API_GATEWAY")
    public List<Events> getEventsByService(String service) {
        return eventsRepository.findByService(parseService(service));
    }


    private Severity parseSeverity(String severity) {
        return Severity.valueOf(normalizeEnumName(severity));
    }

    private Component parseComponent(String component) {
        return Component.valueOf(normalizeEnumName(component));
    }

    private Service parseService(String service) {
        return Service.valueOf(normalizeEnumName(service));
    }

    private String normalizeEnumName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Enum value cannot be null");
        }
        return value.trim()
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase();
    }
}
