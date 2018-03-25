package ru.scheduler.events.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import javax.mail.MessagingException;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.converter.EventConverter;
import ru.scheduler.events.converter.EventNotificationConverter;
import ru.scheduler.events.exception.EventNotFoundException;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.dto.PlaceDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.Event.EventId;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.model.entity.EventNotification;
import ru.scheduler.events.model.entity.EventType;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.repository.EventNotificationRepository;
import ru.scheduler.events.repository.EventRepository;
import ru.scheduler.events.repository.UserEventRepository;
import ru.scheduler.scheduling.model.dto.Mail;
import ru.scheduler.scheduling.model.entity.MailTimerTask;
import ru.scheduler.scheduling.service.MailService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.repository.UserRepository;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */

@Service
public class EventService {

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

    @Synchronized
    public Event updateEvent(Event event) {
        EventInfo eventInfo = event.getInfo();
        Place place = eventInfo.getPlace();

        Place foundPlace = place != null ? placeService.findById(place.getId()) : null;

        if (foundPlace == null && place != null) {
            placeService.addPlace(place);
        } else if (foundPlace != null && !foundPlace.equals(place)) {
            throw new IllegalStateException("CHANGE PLACE WITH EXISTING ID IS FORBIDDEN!");
        }

        EventInfo foundEventInfo = eventInfoService.getEventInfo(eventInfo.getId());
        if (!Objects.equals(eventInfo, foundEventInfo)) {
            eventInfo.setId(0);
            eventInfoService.addEventInfo(eventInfo);
        }

        return eventRepository.persist(event);
    }


    public List<User> getUsers(long id) {
        Event event = eventRepository.findLatestVersionById(id).orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", id));
        List<UserEvent> userEvents = userEventRepository.findByEvent(event);
        List<User> users = userEvents.stream().map(UserEvent::getUser).collect(toList());
        return users;
    }

    public List<Event> getUserEvents(long id) {
        User user = userRepository.findOne(id);
        List<UserEvent> userEvents = userEventRepository.findByUser(user);
        return userEvents.stream()
                .map(UserEvent::getEvent)
                .collect(toList());
    }

    public List<Event> getEvents() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date date = calendar.getTime();
        return extractLatestVersions(eventRepository.findByStartDateGreaterThanEqual(date));
    }

    public List<Event> getApprovedEventsForCalendar() {
        return extractLatestVersions(eventRepository.findByType(EventType.APPROVED));
    }

    public List<Event> getApprovedEvents() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date date = calendar.getTime();
        return extractLatestVersions(
                eventRepository.findByStartDateGreaterThanEqualAndType(date, EventType.APPROVED)
        );
    }

    private static List<Event> extractLatestVersions(List<Event> events) {
        return events.stream()
                .collect(groupingBy(Event::getId, maxBy(comparing(Event::getVersion))))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public List<Event> getEventsByType(String type) {
        EventType eventType = null;
        if (type.equals("WAITED")) {
            eventType = EventType.WAITED;
        } else {
            eventType = EventType.APPROVED;
        }
        return extractLatestVersions(eventRepository.findByType(eventType));
    }

    public Event getEvent(long id, Integer version) {
        if (version == null) {
            return eventRepository.findLatestVersionById(id)
                    .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", id));
        }

        return Optional.ofNullable(eventRepository.findOne(new EventId(id, version)))
                .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", id));
    }

    public List<Event> getAllVersions(long id) {
        return eventRepository.findAllVersionsById(id);
    }

    public UserEvent getUserEvent(long eventId, User user) {
        Event event = eventRepository.findLatestVersionById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", eventId));
        return userEventRepository.findByEventAndUser(event, user);
    }

    public boolean deleteEvent(long id) throws MessagingException {
        Event event = eventRepository.findLatestVersionById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", id));
        List<UserEvent> userEvents = userEventRepository.findByEvent(event);
        for (UserEvent userEvent : userEvents) {
            List<EventNotification> eventNotifications = eventNotificationRepository
                    .findByEvent(userEvent);
            for (EventNotification eventNotification : eventNotifications) {
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
        List<Event> allVersionsById = eventRepository.findAllVersionsById(id);

        eventRepository.delete(allVersionsById);

        return !eventRepository.findLatestVersionById(id).isPresent();
    }

    public UserEvent subscribeEvent(EventNotificationDTO eventNotificationDTO, User user) {
        Event event = eventRepository.findLatestVersionById(eventNotificationDTO.getId())
                .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", eventNotificationDTO.getId()));
        UserEvent userEvent = new UserEvent();
        userEvent.setEvent(event);
        userEvent.setUser(user);
        userEvent.setNotifications(null);
        userEvent = userEventRepository.save(userEvent);
        List<EventNotification> eventNotifications = eventNotificationConverter
                .eventNotificationDtoToEventNotifications(eventNotificationDTO, userEvent);
        for (EventNotification notification : eventNotifications) {
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

            if (today == notificationDate) {
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

    public boolean unsubscribeEvent(long id, User user) {
        Event event = eventRepository.findLatestVersionById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id '%s' not found", id));
        UserEvent userEvent = userEventRepository.findByEventAndUser(event, user);
        List<EventNotification> notifications = userEvent.getNotifications();
        for (EventNotification notification : notifications) {
            eventNotificationRepository.delete(notification);
        }
        userEventRepository.delete(userEvent);
        userEvent = userEventRepository.findOne(userEvent.getId());
        return userEvent == null;
    }

    public List<Event> getBirthDaysByUserNot(User user) {
        List<User> users = userRepository.findByEmailNot(user.getEmail());
        List<Event> events = new ArrayList<>();
        for (User u : users) {
            EventInfo eventInfo = EventInfo.builder()
                    .name("День рождения пользователя " + u.getFirstName() + " " + u.getLastName())
                    .build();
            Date start = u.getBirthday();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(start);
            calendar.set(Calendar.YEAR, year);
            for (int i = 0; i < 10; i++) {
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

    public Event addEvent(Event event) {
        return eventRepository.persist(event);
    }

    public List<Event> addEvents(EventDTO eventDTO) {
        PlaceDTO placeDTO = eventDTO.getPlace();
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

        EventInfo eventInfo = EventInfo.builder()
                .name(eventDTO.getName())
                .description(eventDTO.getDescription())
                .place(place)
                .build();

        eventInfo = eventInfoService.addEventInfo(eventInfo);

        List<Event> events = eventConverter.eventDtoToEvents(eventDTO, eventInfo);

        List<Event> savedEvents = new ArrayList<>();
        for (Event event : events) {
            savedEvents.add(eventRepository.persist(event));
        }
        return savedEvents;
    }
}
