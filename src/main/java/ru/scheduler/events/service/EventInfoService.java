package ru.scheduler.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.repository.EventInfoRepository;

/**
 * Created by Mikhail Yandimirov on 20.04.2017.
 */

@Service
public class EventInfoService {
    @Autowired
    EventInfoRepository eventInfoRepository;

    public EventInfo addEventInfo(EventInfo eventInfo){
        return eventInfoRepository.save(eventInfo);
    }
    public EventInfo getEventInfo(long id){
        return eventInfoRepository.findOne(id);
    }
}
