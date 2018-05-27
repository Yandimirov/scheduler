package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.converter.EventConverter;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceTask implements JavaDelegate {

    private final EventConverter eventConverter;
    private final EventRepository eventRepository;

    @Autowired
    public EventServiceTask(EventConverter eventConverter, EventRepository eventRepository) {
        this.eventConverter = eventConverter;
        this.eventRepository = eventRepository;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);
        EventInfo eventInfo = delegateExecution.getVariable("eventInfo", EventInfo.class);

        List<Event> events = eventConverter.eventDtoToEvents(eventDto, eventInfo);

        List<Event> savedEvents = new ArrayList<>();
        for (Event event : events) {
            savedEvents.add(eventRepository.persist(event));
        }

        delegateExecution.setVariable("events", savedEvents);
    }
}
