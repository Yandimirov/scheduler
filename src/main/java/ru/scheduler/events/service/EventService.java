package ru.scheduler.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.converter.EventConverter;
import ru.scheduler.events.converter.EventNotificationConverter;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.dto.PlaceDTO;
import ru.scheduler.events.model.entity.*;
import ru.scheduler.events.repository.EventNotificationRepository;
import ru.scheduler.events.repository.EventRepository;
import ru.scheduler.events.repository.UserEventRepository;
import ru.scheduler.scheduling.model.dto.Mail;
import ru.scheduler.scheduling.model.entity.MailTimerTask;
import ru.scheduler.scheduling.service.MailService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.repository.UserRepository;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */

@Service
public class EventService
{
    @Autowired
    EventRepository eventRepository;

    @Autowired
    PlaceService placeService;

    @Autowired
    EventInfoService eventInfoService;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    EventNotificationRepository eventNotificationRepository;

    @Autowired
    MailService mailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventConverter eventConverter;

    @Autowired
    EventNotificationConverter eventNotificationConverter;

    public Event updateEvent(Event event){
        Event srcEvent = eventRepository.findOne(event.getId());
        if(srcEvent.getType() == event.getType() || event.getType() == null){
            event.setType(EventType.APPROVED);
            EventInfo eventInfo = event.getInfo();
            Long countEventsWithInfo = eventRepository.countByEventInfo(eventInfo.getId());
            if(countEventsWithInfo > 1){
                eventInfo.setId(0);

            }
            eventInfoService.addEventInfo(eventInfo);
            if(placeService.findById(event.getInfo().getPlace().getId()) == null){
                placeService.addPlace(event.getInfo().getPlace());
            }
        }
        return eventRepository.save(event);
    }

    public List<User> getUsers(long id){
        Event event = eventRepository.findOne(id);
        List<User> users = new ArrayList<>();
        if(event != null){
            List<UserEvent> userEvents = userEventRepository.findByEvent(event);
            users = userEvents.stream().map(UserEvent::getUser).collect(Collectors.toList());
        }
        return users;
    }

    public List<Event> getUserEvents(long id){
        User user = userRepository.findOne(id);
        List<UserEvent> userEvents = userEventRepository.findByUser(user);
        List<Event> events = new ArrayList<>();
        for(UserEvent userEvent : userEvents){
            events.add(eventRepository.findOne(userEvent.getEvent().getId()));
        }
        return events;
    }

    public List<Event> getEvents(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date date = calendar.getTime();
        return eventRepository.findByStartDateGreaterThanEqual(date);
    }

    public List<Event> getApprovedEventsForCalendar(){
        return eventRepository.findByType(EventType.APPROVED);
    }

    public List<Event> getApprovedEvents(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date date = calendar.getTime();
        return eventRepository.findByStartDateGreaterThanEqualAndType(date, EventType.APPROVED);
    }

    public List<Event> getEventsByType(String type){
        EventType eventType = null;
        if(type.equals("WAITED")){
            eventType = EventType.WAITED;
        } else {
            eventType = EventType.APPROVED;
        }
        return eventRepository.findByType(eventType);
    }

    public Event getEvent(long id){
        return eventRepository.findOne(id);
    }

    public UserEvent getUserEvent(long eventId, User user){
        Event event = eventRepository.findOne(eventId);
        return userEventRepository.findByEventAndUser(event, user);
    }

    public boolean deleteEvent(long id) throws MessagingException {
        Event event = eventRepository.findOne(id);
        if(event != null){
            List<UserEvent> userEvents = userEventRepository.findByEvent(event);
            for(UserEvent userEvent : userEvents){
                List<EventNotification> eventNotifications = eventNotificationRepository.findByEvent(userEvent);
                for(EventNotification eventNotification : eventNotifications){
                    eventNotificationRepository.delete(eventNotification);
                }
                StringBuilder mailText = new StringBuilder();
                mailText.append("Event with name ").append(userEvent.getEvent().getInfo().getName())
                        .append(" was removed!");
                Mail mail = Mail.builder()
                        .to(userEvent.getUser().getEmail())
                        .subject("Event was removed")
                        .text(mailText.toString())
                        .build();
                mailService.asyncSend(mail);
                userEventRepository.delete(userEvent);
            }
            eventRepository.delete(event);
        }
        event = eventRepository.findOne(id);
        return event == null;
    }

    public UserEvent subscribeEvent(EventNotificationDTO eventNotificationDTO, User user){
        Event event = eventRepository.findOne(eventNotificationDTO.getId());
        UserEvent userEvent = new UserEvent();
        userEvent.setEvent(event);
        userEvent.setUser(user);
        userEvent.setNotifications(null);
        userEvent = userEventRepository.save(userEvent);
        List<EventNotification> eventNotifications = eventNotificationConverter.eventNotificationDtoToEventNotifications(eventNotificationDTO, userEvent);
        for(EventNotification notification : eventNotifications){
            eventNotificationRepository.save(notification);
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 2);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long today = calendar.getTimeInMillis();

            calendar.setTime(notification.getWhen());

            calendar.set(Calendar.HOUR_OF_DAY, 2);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long notificationDate = calendar.getTimeInMillis();

            if(today == notificationDate){
                Timer timer = new Timer();
                MailTimerTask task = new MailTimerTask();
                task.setNotificationId(notification.getId());
                task.setEventNotificationRepository(eventNotificationRepository);
                task.setMailService(mailService);
                timer.schedule(task, notification.getWhen());
            }

        }
        //userEvent.setNotifications(eventNotifications);
        return userEvent;
    }

    public boolean unsubscribeEvent(long id, User user){
        Event event = eventRepository.findOne(id);
        UserEvent userEvent = userEventRepository.findByEventAndUser(event, user);
        List<EventNotification> notifications = userEvent.getNotifications();
        for(EventNotification notification : notifications){
            eventNotificationRepository.delete(notification);
        }
        userEventRepository.delete(userEvent);
        userEvent = userEventRepository.findOne(userEvent.getId());
        return userEvent == null;
    }

    public List<Event> getBirthDaysByUserNot(User user){
        List<User> users = userRepository.findByEmailNot(user.getEmail());
        List<Event> events = new ArrayList<>();
        for(User u : users){
            EventInfo eventInfo = EventInfo.builder()
                    .name("День рождения пользователя " + u.getFirstName() + " " + u.getLastName())
                    .build();
            Date start = u.getBirthday();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(start);
            calendar.set(Calendar.YEAR, year);
            for(int i = 0; i < 10; i++){
                Event event = new Event();
                event.setInfo(eventInfo);
                event.setStartDate(new Date(calendar.getTimeInMillis()));
                event.setEndDate(new Date(calendar.getTimeInMillis() + 86400000));
                events.add(event);
                calendar.add(Calendar.YEAR, 1);
            }
        }
        return events;
    }

    public Event addEvent(Event event){
        return eventRepository.save(event);
    }

    public List<Event> addEvents(EventDTO eventDTO){
        PlaceDTO placeDTO = eventDTO.getPlace();
        Place place = null;
        if(placeDTO != null){
            place = Place.builder()
                    .id(placeDTO.getId())
                    .name(placeDTO.getName())
                    .lat(placeDTO.getLat())
                    .lon(placeDTO.getLng())
                    .build();


            if(null != placeService.findById(place.getId())){
                place = placeService.findById(place.getId());
            } else {
                place = placeService.addPlace(place);
            }
        }

        EventInfo eventInfo = EventInfo.builder()
                .name(eventDTO.getName())
                .description(eventDTO.getDescription())
                .place(place)
                .build();

        eventInfo = eventInfoService.addEventInfo(eventInfo);

        List<Event> events = eventConverter.eventDtoToEvents(eventDTO, eventInfo);

        List<Event> savedEvents = new ArrayList<>();
        for(Event event : events){
            savedEvents.add(eventRepository.save(event));
        }
        return savedEvents;
    }
}
