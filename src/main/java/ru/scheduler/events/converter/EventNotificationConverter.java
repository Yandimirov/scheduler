package ru.scheduler.events.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.entity.EventNotification;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.repository.EventNotificationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EventNotificationConverter {
    @Autowired
    EventNotificationRepository eventNotificationRepository;

    public List<EventNotification> eventNotificationDtoToEventNotifications(EventNotificationDTO eventNotificationDTO, UserEvent userEvent){
        List<EventNotification> eventNotifications = new ArrayList<>();
        List<Date> notifications = eventNotificationDTO.getNotifications();
        for(Date date : notifications){
            EventNotification notification = new EventNotification();
            notification.setEvent(userEvent);
            notification.setWhen(date);
            eventNotifications.add(notification);
        }
        return eventNotifications;
    }
}
