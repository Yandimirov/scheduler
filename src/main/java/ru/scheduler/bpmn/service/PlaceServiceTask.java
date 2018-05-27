package ru.scheduler.bpmn.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.PlaceDTO;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.service.PlaceService;

@Service
public class PlaceServiceTask implements JavaDelegate {

    private final PlaceService placeService;
 
    @Autowired
    public PlaceServiceTask(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        EventDTO eventDto = delegateExecution.getVariable("eventDto", EventDTO.class);
        PlaceDTO placeDTO = eventDto.getPlace();
        Place place = null;
        if (placeDTO != null) {
            place = Place.builder()
                    .id(placeDTO.getId())
                    .name(placeDTO.getName())
                    .lat(placeDTO.getLat())
                    .lon(placeDTO.getLng())
                    .build();

            if (null != placeService.findById(place.getId())) {
                place = placeService.findById(place.getId());
            } else {
                place = placeService.addPlace(place);
            }
        }

        delegateExecution.setVariable("place", place);
    }
}
