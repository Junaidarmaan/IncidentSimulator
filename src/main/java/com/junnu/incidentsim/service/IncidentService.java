package com.junnu.incidentsim.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
    
import com.junnu.incidentsim.ai.assistants.IncidentAssistant;
import com.junnu.incidentsim.dto.EventDTO;
import com.junnu.incidentsim.dto.IncidentPlan;
import com.junnu.incidentsim.entity.Events;
import com.junnu.incidentsim.repository.EventsRepository;

@Service 
public class IncidentService {
    
    IncidentAssistant assistant;
    EventsRepository events;
    public  IncidentService(IncidentAssistant assistant,EventsRepository events ){
        this.assistant = assistant;
        this.events = events;
    }

    public IncidentPlan simulateIncident(String msg){
        IncidentPlan plan = assistant.chat(msg);
        processPlan(plan);
        return plan;
    }
    private void processPlan(IncidentPlan plan){
        Long incidentId = getNextIncidentId();
        for(EventDTO event : plan.getEvents()){
            Events e = EventDTOToEvents(event);
            e.setIncidentId(incidentId);
            events.save(e);
        }
    }
    public  Events EventDTOToEvents(EventDTO dto){
        Events event = new Events();
        event.setComponent(dto.getComponent());
        event.setMessage(dto.getMessage());
        event.setService(dto.getService());
        event.setSeverity(dto.getSeverity());
        event.setStackTrace(dto.getStackTrace());
        event.setTimestamp(dto.getTimestamp());
        return event;
    }
    public  Long getNextIncidentId(){
        Long id = events.findMaxIncidentId();
        if(id == null){
            return 1L;
        }
        return id+1;
    }

    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            try {
                List<Events> records = events.findAll(Sort.by(Sort.Direction.ASC, "id"));

                for (int i = 0; i < records.size(); i++) {
                    emitter.send(SseEmitter.event()
                            .name("event")
                            .data(  records.get(i)));

                    if (i < records.size() - 1) {
                        Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 3001));
                    }
                }

                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }
}
