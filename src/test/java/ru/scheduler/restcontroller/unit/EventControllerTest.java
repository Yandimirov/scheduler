package ru.scheduler.restcontroller.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.Event.EventId;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.restcontroller.EventController;
import ru.scheduler.events.service.EventService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.users.service.JwtService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mikhail Yandimirov on 13.05.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {
    @Mock
    EventService eventService;

    @Mock
    JwtService jwtService;

    @InjectMocks
    EventController eventController;

    String TOKEN_HEADER;
    Event event;
    String token;
    User user;
    UserEvent userEvent;
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    EventDTO eventDTO;
    EventNotificationDTO eventNotificationDTO;

    @Before
    public void before(){
        TOKEN_HEADER = "x-auth-token";
        token = "123";
        user = new User();
        user.setId(1L);
        ReflectionTestUtils.setField(eventController, "TOKEN_HEADER", TOKEN_HEADER);
        event = new Event();
        event.setCompositeId(new EventId(1L));
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        headers.put(TOKEN_HEADER, tokens);
        userEvent = new UserEvent();
        userEvent.setId(1L);
        userEvent.setUser(user);
        userEvent.setEvent(event);
        eventDTO = new EventDTO();
        eventNotificationDTO = new EventNotificationDTO();
    }

    @Test
    public void getCalendarEvents() throws Exception {
        when(eventService.getApprovedEventsForCalendar()).thenReturn(new ArrayList<Event>());
        eventController.getCalendarEvents();
        verify(eventService).getApprovedEventsForCalendar();
    }

    @Test
    public void getEvents() throws Exception {
        when(eventService.getApprovedEvents()).thenReturn(new ArrayList<Event>());
        eventController.getEvents(null);
        verify(eventService).getApprovedEvents();
    }

    @Test
    public void updateEvent() throws Exception {
        when(eventService.updateEvent(event)).thenReturn(event);
        eventController.updateEvent(event);
        verify(eventService).updateEvent(event);
    }

    @Test
    public void getEventsByMonth() throws Exception {

    }

    @Test
    public void getEventsByType() throws Exception {
        String type = "TYPE";
        when(eventService.getEventsByType(type)).thenReturn(new ArrayList<Event>());
        eventController.getEventsByType(type);
        verify(eventService).getEventsByType(type);
    }

    @Test
    public void getBirtdays() throws Exception {
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.getBirthDaysByUserNot(user)).thenReturn(new ArrayList<Event>());
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        eventController.getBirtdays(request);
        verify(jwtService).getUser(token);
        verify(eventService).getBirthDaysByUserNot(user);
    }

    @Test
    public void getUserEvent() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.getUserEvent(userEvent.getEvent().getId(), user)).thenReturn(userEvent);
        eventController.getUserEvent(request, userEvent.getEvent().getId());
        verify(jwtService).getUser(token);
        verify(eventService).getUserEvent(userEvent.getEvent().getId(), user);
    }

    @Test
    public void addEvent() throws Exception {
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.addEvents(eventDTO)).thenReturn(new ArrayList<Event>());
        eventController.addEvent(eventDTO, token);
        verify(jwtService).getUser(token);
        verify(eventService).addEvents(eventDTO);
    }

    @Test
    public void getEvent() throws Exception {
        when(eventService.getEvent(event.getId(), null)).thenReturn(event);
        eventController.getEvent(event.getId(), null);
        verify(eventService).getEvent(event.getId(), null);
    }

    @Test
    public void unsubscribeEvent() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.unsubscribeEvent(event.getId(), user)).thenReturn(true);
        eventController.unsubscribeEvent(request, event.getId());
        verify(jwtService).getUser(token);
        verify(eventService).unsubscribeEvent(event.getId(), user);
    }

    @Test
    public void subscribeEvent() throws Exception {
        RequestEntity<EventNotificationDTO> request = new RequestEntity<>(eventNotificationDTO, headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.subscribeEvent(eventNotificationDTO, user)).thenReturn(new UserEvent());
        eventController.subscribeEvent(eventNotificationDTO, event.getId(), "12345");
        verify(jwtService).getUser(token);
        verify(eventService).subscribeEvent(eventNotificationDTO, user);
    }

    @Test
    public void getUserEvents() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(eventService.getUserEvents(user.getId())).thenReturn(new ArrayList<Event>());
        eventController.getUserEvent(request, user.getId());
        verify(eventService).getUserEvents(user.getId());
    }

    @Test
    public void getSubscribedUsers() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(eventService.getUsers(event.getId())).thenReturn(new ArrayList<User>());
        eventController.getSubscribedUsers(request, event.getId());
        verify(eventService).getUsers(event.getId());
    }

    @Test
    public void deleteEventByModerator() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(eventService.deleteEvent(event.getId())).thenReturn(true);
        user.setRole(UserRole.MODERATOR);
        eventController.deleteEvent(request, event.getId());
        verify(jwtService).getUser(token);
        verify(eventService).deleteEvent(event.getId());
    }

    @Test
    public void deleteEventByUser() throws Exception{
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        eventController.deleteEvent(request, event.getId());
        verify(jwtService).getUser(token);
    }

}