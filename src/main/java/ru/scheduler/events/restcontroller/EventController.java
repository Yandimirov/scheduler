package ru.scheduler.events.restcontroller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.config.View;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventWithUserStatus;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.model.entity.UserEventStatus;
import ru.scheduler.events.repository.UserEventRepository;
import ru.scheduler.events.service.EventService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.UserService;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventRepository userEventRepository;

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;


    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event/calendar", method = RequestMethod.GET)
    public List<Event> getCalendarEvents() {
        return eventService.getApprovedEventsForCalendar();
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public List<Event> getEvents(RequestEntity<?> request) {
        return eventService.getEvents();
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value ="/eventWithStatus", method = RequestMethod.GET)
    public List<EventWithUserStatus> getEventsWithStatuses(@RequestHeader("x-auth-token") String token) {
        User user = jwtService.getUser(token);
        return eventService.getAllUserEvents(user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.PUT)
    public Event updateEvent(@RequestBody Event event) {
        return eventService.updateEvent(event);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event/month/{number}", method = RequestMethod.GET)
    public List<Event> getEventsByMonth(@PathVariable long number) {
        return null;
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/birthday", method = RequestMethod.GET)
    public List<Event> getBirtdays(RequestEntity<?> request) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return eventService.getBirthDaysByUserNot(user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.GET)
    public UserEvent getUserEvent(RequestEntity<?> request, @PathVariable long id) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return eventService.getUserEvent(id, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public List<Event> addEvent(@RequestBody EventDTO eventDTO, @RequestHeader("x-auth-token") String token) {
        User user = jwtService.getUser(token);
        eventDTO.setCreatedBy(user);

        List<Event> events = eventService.addEvents(eventDTO);
        events.stream()
                .map(event -> new EventNotificationDTO(event.getId(), Collections.emptyList()))
                .forEach(eventNotificationDTO -> eventService.subscribeEvent(eventNotificationDTO, user));

        List<Long> userIds = eventDTO.getUserIds();
        events.forEach(event -> userIds.forEach(userId -> {
            User invitedUser = userService.getUserById(userId);
            UserEvent userEvent = new UserEvent();
            userEvent.setStatus(UserEventStatus.WAITED);
            userEvent.setEvent(event);
            userEvent.setUser(invitedUser);
            userEventRepository.save(userEvent);
        }));

        eventService.sendNewEventsMailToUsers(userIds);
        return events;
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}/decline", method = RequestMethod.POST)
    public void rejectEvent(@PathVariable long id, @RequestHeader("x-auth-token") String token) {
        User user = jwtService.getUser(token);
        eventService.rejectEvent(id, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable long id, @RequestParam(value = "version", required = false) Integer version) {
        return eventService.getEvent(id, version);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/versions/{id}", method = RequestMethod.GET)
    public List<Event> getAllVersions(@PathVariable long id) {
        return eventService.getAllVersions(id);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.DELETE)
    public boolean unsubscribeEvent(RequestEntity<?> request, @PathVariable long id) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return eventService.unsubscribeEvent(id, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.POST)
    public UserEvent subscribeEvent(
            @RequestBody EventNotificationDTO dto,
            @PathVariable long id,
            @RequestHeader("x-auth-token") String token) {

        User user = jwtService.getUser(token);
        return eventService.subscribeEvent(dto, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/{id}/event", method = RequestMethod.GET)
    public List<Event> getUserEventsByStatus(RequestEntity<?> request,
                                             @PathVariable long id,
                                             @RequestParam(name = "status", required = false, defaultValue = "ACCEPTED") String status) {
        return eventService.getUserEventsByStatus(id, status);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}/users", method = RequestMethod.GET)
    public List<User> getSubscribedUsers(RequestEntity<?> request, @PathVariable long id) {
        return eventService.getUsers(id);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}", method = RequestMethod.DELETE)
    public boolean deleteEvent(@PathVariable long id, @RequestHeader("x-auth-token") String token) {
        User user = jwtService.getUser(token);
        Event event = getEvent(id, null);
        return Objects.equals(user, event.getInfo().getCreatedBy()) && eventService.deleteEvent(id);
    }

}
