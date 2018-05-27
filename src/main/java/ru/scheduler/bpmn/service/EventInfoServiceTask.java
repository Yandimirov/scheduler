package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.service.EventInfoService;

@Service
public class EventInfoServiceTask implements JavaDelegate {

    private final EventInfoService eventInfoService;

    @Autowired
    public EventInfoServiceTask(EventInfoService eventInfoService) {
        this.eventInfoService = eventInfoService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);
        Place place = delegateExecution.getVariable("place", Place.class);

        EventInfo eventInfo = EventInfo.builder()
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .createdBy(eventDto.getCreatedBy())
                .place(place)
                .build();

        eventInfo = eventInfoService.addEventInfo(eventInfo);

        delegateExecution.setVariable("eventInfo", eventInfo);
    }
}
