package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.converter.EventConverter;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.repository.EventRepository;
import ru.scheduler.events.service.EventService;
import ru.scheduler.users.model.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EventServiceTask implements JavaDelegate {

    private final EventConverter eventConverter;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Autowired
    public EventServiceTask(EventConverter eventConverter, EventRepository eventRepository, EventService eventService) {
        this.eventConverter = eventConverter;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);
        EventInfo eventInfo = delegateExecution.getVariable("eventInfo", EventInfo.class);

        List<Event> events = eventConverter.eventDtoToEvents(eventDto, eventInfo);

        User user = eventDto.getCreatedBy();

        List<Event> savedEvents = new ArrayList<>();
        for (Event event : events) {
            savedEvents.add(eventRepository.persist(event));
        }

        savedEvents.stream()
                .map(event -> new EventNotificationDTO(event.getId(), Collections.emptyList()))
                .forEach(eventNotificationDTO -> eventService.subscribeEvent(eventNotificationDTO, user));

        delegateExecution.setVariable("events", savedEvents);
    }
}
