package ru.scheduler.events.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.RepeatDTO;
import ru.scheduler.events.model.dto.RepeatFreq;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.service.EventInfoService;
import ru.scheduler.events.service.PlaceService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class EventConverter {
    @Autowired
    PlaceService placeService;

    @Autowired
    EventInfoService eventInfoService;

    public List<Event> eventDtoToEvents(EventDTO eventDTO, EventInfo eventInfo){
        List<Event> events = new ArrayList<>();

        RepeatDTO repeatDTO = eventDTO.getRepeats();
        if(repeatDTO != null){
            int repeatValue = repeatDTO.getValue();
            RepeatFreq freq = repeatDTO.getFreq();
            long repeatDate = repeatDTO.getUntil().getTime();
            long startDate = eventDTO.getStartDate().getTime();
            long endDate = eventDTO.getEndDate().getTime();

            long ms = 0;
            Calendar start = Calendar.getInstance();
            start.setTime(eventDTO.getStartDate());
            Calendar repeat = Calendar.getInstance();
            repeat.setTime(eventDTO.getStartDate());
            if(freq == RepeatFreq.DAY){
                ms = 86400000 * repeatValue;
            } else if (freq == RepeatFreq.WEEK){
                ms = 604800000 * repeatValue;
            } else if (freq == RepeatFreq.MONTH){
                repeat.add(Calendar.MONTH, repeatValue);
                ms = repeat.getTimeInMillis() - start.getTimeInMillis();
            } else if (freq == RepeatFreq.YEAR){
                repeat.add(Calendar.YEAR, repeatValue);
                ms = repeat.getTimeInMillis() - start.getTimeInMillis();
            }

            long tmpStartDate = startDate;
            long tmpEndDate = endDate;

            while(tmpStartDate < repeatDate){
                Event event = new Event();
                event.setInfo(eventInfo);
                event.setStartDate(new Date(tmpStartDate));
                event.setEndDate(new Date(tmpEndDate));
                event.setType(eventDTO.getType());
                tmpStartDate += ms;
                tmpEndDate += ms;
                events.add(event);
            }
        } else {
            Event event = new Event();
            event.setInfo(eventInfo);
            event.setStartDate(eventDTO.getStartDate());
            event.setEndDate(eventDTO.getEndDate());
            event.setType(eventDTO.getType());
            events.add(event);
        }
        return events;
    }
}

