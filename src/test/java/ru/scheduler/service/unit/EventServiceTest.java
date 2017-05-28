package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.events.converter.EventConverter;
import ru.scheduler.events.converter.EventNotificationConverter;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.dto.PlaceDTO;
import ru.scheduler.events.model.entity.*;
import ru.scheduler.events.repository.EventNotificationRepository;
import ru.scheduler.events.repository.EventRepository;
import ru.scheduler.events.repository.UserEventRepository;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.events.service.EventInfoService;
import ru.scheduler.events.service.EventService;
import ru.scheduler.scheduling.service.MailService;
import ru.scheduler.events.service.PlaceService;
import ru.scheduler.users.model.entity.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    @Mock
    EventRepository eventRepository;

    @Mock
    PlaceService placeService;

    @Mock
    EventInfoService eventInfoService;

    @Mock
    UserEventRepository userEventRepository;

    @Mock
    EventNotificationRepository eventNotificationRepository;

    @Mock
    MailService mailService;

    @Mock
    UserRepository userRepository;

    @Mock
    EventConverter eventConverter;

    @Mock
    EventNotificationConverter eventNotificationConverter;

    @InjectMocks
    EventService eventService;

    Event event;
    Event newEvent;
    EventInfo eventInfo;
    Place place;
    User user;
    List<UserEvent> userEvents;
    UserEvent userEvent;
    Date date;
    List<EventNotification> eventNotifications;
    EventNotification eventNotification;
    EventNotificationDTO eventNotificationDTO;
    List<Event> events;
    EventDTO eventDTO;

    @Before
    public void setUp() throws Exception {
        place = Place.builder()
                .id("1")
                .lat(1.0)
                .lon(1.0)
                .build();

        eventInfo = EventInfo.builder()
                    .name("123")
                    .id(0)
                    .description("123")
                    .place(place)
                    .build();

        event = new Event();
        event.setId(1L);
        event.setType(EventType.APPROVED);
        event.setInfo(eventInfo);

        newEvent = new Event();
        newEvent.setId(1L);
        newEvent.setType(EventType.WAITED);
        newEvent.setInfo(eventInfo);

        user = new User();
        user.setId(1L);
        user.setEmail("linux95@bk.ru");

        userEvent = new UserEvent();
        userEvent.setEvent(event);
        userEvent.setUser(user);

        userEvents = new ArrayList<>();
        userEvents.add(userEvent);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        date = calendar.getTime();

        eventNotification = new EventNotification();
        eventNotification.setId(1L);
        eventNotification.setEvent(userEvent);
        eventNotification.setWhen(date);

        eventNotifications = new ArrayList<>();
        eventNotifications.add(eventNotification);

        List<Date> dates = new ArrayList<>();
        dates.add(date);

        eventNotificationDTO = new EventNotificationDTO();
        eventNotificationDTO.setId(1);
        eventNotificationDTO.setNotifications(dates);

        events = new ArrayList<>();
        events.add(event);

        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setName(place.getName());
        placeDTO.setId(place.getId());
        placeDTO.setLat(place.getLat());
        placeDTO.setLng(place.getLon());

        eventDTO = new EventDTO();
        eventDTO.setName("123");
        eventDTO.setDescription("123");
        eventDTO.setPlace(placeDTO);
    }

    @Test
    public void updateEvent() throws Exception {
        newEvent.setType(EventType.APPROVED);
        when(eventRepository.findOne(newEvent.getId())).thenReturn(event);
        when(eventInfoService.addEventInfo(newEvent.getInfo())).thenReturn(newEvent.getInfo());
        when(placeService.addPlace(newEvent.getInfo().getPlace())).thenReturn(newEvent.getInfo().getPlace());
        when(eventRepository.save(newEvent)).thenReturn(newEvent);

        eventService.updateEvent(newEvent);

        verify(eventRepository).findOne(newEvent.getId());
        verify(eventInfoService).addEventInfo(newEvent.getInfo());
        verify(placeService).addPlace(newEvent.getInfo().getPlace());
        verify(eventRepository).save(newEvent);
        newEvent.setType(EventType.WAITED);
    }

    @Test
    public void aproveEvent() throws Exception{
        when(eventRepository.findOne(newEvent.getId())).thenReturn(event);
        when(eventRepository.save(newEvent)).thenReturn(newEvent);


        eventService.updateEvent(newEvent);

        verify(eventRepository).findOne(newEvent.getId());
        verify(eventRepository).save(newEvent);
    }

    @Test
    public void getUsers() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        when(userEventRepository.findByEvent(event)).thenReturn(new ArrayList<UserEvent>());

        eventService.getUsers(event.getId());

        verify(eventRepository).findOne(event.getId());
        verify(userEventRepository).findByEvent(event);
    }

    @Test
    public void getUserEvents() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userEventRepository.findByUser(user)).thenReturn(userEvents);
        for(UserEvent uv : userEvents){
            when(eventRepository.findOne(uv.getEvent().getId())).thenReturn(uv.getEvent());
        }

        eventService.getUserEvents(user.getId());

        verify(userRepository).findOne(user.getId());
        verify(userEventRepository).findByUser(user);
        for(UserEvent uv: userEvents){
            verify(eventRepository).findOne(uv.getEvent().getId());
        }
    }

    @Test
    public void getEvents() throws Exception {
        when(eventRepository.findByStartDateGreaterThanEqual(date)).thenReturn(new ArrayList<Event>());
        eventService.getEvents();
        verify(eventRepository).findByStartDateGreaterThanEqual(date);
    }

    @Test
    public void getApprovedEventsForCalendar() throws Exception {
        when(eventRepository.findByType(EventType.APPROVED)).thenReturn(new ArrayList<Event>());
        eventService.getApprovedEventsForCalendar();
        verify(eventRepository).findByType(EventType.APPROVED);
    }

    @Test
    public void getApprovedEvents() throws Exception {
        when(eventRepository.findByStartDateGreaterThanEqualAndType(date, EventType.APPROVED)).thenReturn(new ArrayList<Event>());
        eventService.getApprovedEvents();
        verify(eventRepository).findByStartDateGreaterThanEqualAndType(date, EventType.APPROVED);
    }

    @Test
    public void getEventsByType() throws Exception {
        when(eventRepository.findByType(EventType.APPROVED)).thenReturn(new ArrayList<Event>());
        eventService.getEventsByType(EventType.APPROVED.toString());
        verify(eventRepository).findByType(EventType.APPROVED);
    }

    @Test
    public void getEvent() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        eventService.getEvent(event.getId());
        verify(eventRepository).findOne(event.getId());
    }

    @Test
    public void getUserEvent() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        when(userEventRepository.findByEventAndUser(event, user)).thenReturn(userEvent);
        eventService.getUserEvent(event.getId(), user);
        verify(eventRepository).findOne(event.getId());
        verify(userEventRepository).findByEventAndUser(event, user);
    }

    @Test
    public void deleteEvent() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        when(userEventRepository.findByEvent(event)).thenReturn(userEvents);
        for(UserEvent uv: userEvents){
            when(eventNotificationRepository.findByEvent(uv)).thenReturn(eventNotifications);
        }

        eventService.deleteEvent(event.getId());

        verify(eventRepository, times(2)).findOne(event.getId());
        verify(userEventRepository).findByEvent(event);
        for(UserEvent uv: userEvents){
            verify(eventNotificationRepository).findByEvent(uv);
        }
    }

    @Test
    public void subscribeEvent() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        when(userEventRepository.save(userEvent)).thenReturn(userEvent);
        when(eventNotificationConverter.eventNotificationDtoToEventNotifications(eventNotificationDTO, userEvent)).thenReturn(eventNotifications);
        for(EventNotification notification : eventNotifications){
            when(eventNotificationRepository.save(notification)).thenReturn(notification);
        }

        eventService.subscribeEvent(eventNotificationDTO, user);

        verify(eventRepository).findOne(event.getId());
        verify(userEventRepository).save(userEvent);
        verify(eventNotificationConverter).eventNotificationDtoToEventNotifications(eventNotificationDTO, userEvent);
        for(EventNotification notification : eventNotifications){
            verify(eventNotificationRepository).save(notification);
        }
    }

    @Test
    public void unsubscribeEvent() throws Exception {
        when(eventRepository.findOne(event.getId())).thenReturn(event);
        when(userEventRepository.findByEventAndUser(event, user)).thenReturn(userEvent);
        userEvent.setNotifications(eventNotifications);
        userEvent.setId(1L);
        when(userEventRepository.findOne(userEvent.getId())).thenReturn(null);

        eventService.unsubscribeEvent(event.getId(), user);

        verify(eventRepository).findOne(event.getId());
        verify(userEventRepository).findByEventAndUser(event, user);
        verify(userEventRepository).findOne(userEvent.getId());
    }

    @Test
    public void getBirthDaysByUserNot() throws Exception {
        when(userRepository.findByEmailNot(user.getEmail())).thenReturn(new ArrayList<User>());
        eventService.getBirthDaysByUserNot(user);
        verify(userRepository).findByEmailNot(user.getEmail());
    }

    @Test
    public void addEvent() throws Exception {
        when(eventRepository.save(event)).thenReturn(event);
        eventService.addEvent(event);
        verify(eventRepository).save(event);
    }

    @Test
    public void addEvents() throws Exception {
        when(placeService.findById(place.getId())).thenReturn(null);
        when(placeService.addPlace(place)).thenReturn(place);
        when(eventInfoService.addEventInfo(eventInfo)).thenReturn(eventInfo);
        when(eventConverter.eventDtoToEvents(eventDTO, eventInfo)).thenReturn(events);
        for(Event e : events){
            when(eventRepository.save(e)).thenReturn(e);
        }

        eventService.addEvents(eventDTO);

        verify(placeService).findById(place.getId());
        verify(placeService).addPlace(place);
        verify(eventInfoService).addEventInfo(eventInfo);
        verify(eventConverter).eventDtoToEvents(eventDTO, eventInfo);
        for(Event e : events){
            verify(eventRepository).save(e);
        }
    }

    @Test
    public void addEvents2() throws Exception {
        when(placeService.findById(place.getId())).thenReturn(place);
        when(eventInfoService.addEventInfo(eventInfo)).thenReturn(eventInfo);
        when(eventConverter.eventDtoToEvents(eventDTO, eventInfo)).thenReturn(events);
        for(Event e : events){
            when(eventRepository.save(e)).thenReturn(e);
        }

        eventService.addEvents(eventDTO);

        verify(placeService, times(2)).findById(place.getId());
        verify(eventInfoService).addEventInfo(eventInfo);
        verify(eventConverter).eventDtoToEvents(eventDTO, eventInfo);
        for(Event e : events){
            verify(eventRepository).save(e);
        }
    }

}