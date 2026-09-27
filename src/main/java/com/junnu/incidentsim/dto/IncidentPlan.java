package com.junnu.incidentsim.dto;

import java.util.List;



public class IncidentPlan {
    List<EventDTO> events;
    String responseMessage;
    public List<EventDTO> getEvents() {
        return events;
    }
    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}
