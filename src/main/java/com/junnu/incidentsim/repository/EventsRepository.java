package com.junnu.incidentsim.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.junnu.incidentsim.entity.Events;
import com.junnu.incidentsim.enums.Component;
import com.junnu.incidentsim.enums.Service;
import com.junnu.incidentsim.enums.Severity;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    Page<Events> findAll(Pageable pageable);

    List<Events> findBySeverity(Severity severity);

    List<Events> findByComponent(Component component);

    List<Events> findByService(Service service);

    @Query(value = "SELECT MAX(incident_id) FROM events", nativeQuery = true)
    Long findMaxIncidentId();
    
}