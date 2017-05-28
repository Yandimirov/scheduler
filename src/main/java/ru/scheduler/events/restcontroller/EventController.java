package ru.scheduler.events.restcontroller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventType;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.config.View;
import ru.scheduler.events.service.EventService;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event/calendar", method = RequestMethod.GET)
    public List<Event> getCalendarEvents(){
        return eventService.getApprovedEventsForCalendar();
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public List<Event> getEvents(RequestEntity<?> request){
        return eventService.getApprovedEvents();
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.PUT)
    public Event updateEvent(RequestEntity<Event> request){
        return eventService.updateEvent(request.getBody());
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event/month/{number}", method = RequestMethod.GET)
    public List<Event> getEventsByMonth(@PathVariable long number){
        return null;
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event/type/{type}", method = RequestMethod.GET)
    public List<Event> getEventsByType(@PathVariable String type){
        return eventService.getEventsByType(type);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/birthday", method = RequestMethod.GET)
    public List<Event> getBirtdays(RequestEntity<?> request){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        return eventService.getBirthDaysByUserNot(user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.GET)
    public UserEvent getUserEvent(RequestEntity<?> request, @PathVariable long id){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        return eventService.getUserEvent(id, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public List<Event> addEvent(RequestEntity<EventDTO> request){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        EventDTO eventDTO = request.getBody();
        if(user.getRole() == UserRole.USER){
            eventDTO.setType(EventType.WAITED);
        } else {
            eventDTO.setType(EventType.APPROVED);
        }
        return eventService.addEvents(eventDTO);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable long id){
        return eventService.getEvent(id);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.DELETE)
    public boolean unsubscribeEvent(RequestEntity<?> request, @PathVariable long id){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        return eventService.unsubscribeEvent(id, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/event/{id}", method = RequestMethod.POST)
    public UserEvent subscribeEvent(RequestEntity<EventNotificationDTO> request, @PathVariable long id){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        EventNotificationDTO eventNotificationDTO = request.getBody();
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        return eventService.subscribeEvent(eventNotificationDTO, user);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "user/{id}/event", method = RequestMethod.GET)
    public List<Event> getUserEvents(RequestEntity<?> request, @PathVariable long id){
        return eventService.getUserEvents(id);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}/users", method = RequestMethod.GET)
    public List<User> getSubscribedUsers(RequestEntity<?> request, @PathVariable long id){
        return eventService.getUsers(id);
    }

    @JsonView(View.EVENT.class)
    @RequestMapping(value = "event/{id}", method = RequestMethod.DELETE)
    public boolean deleteEvent(RequestEntity<?> request, @PathVariable long id) throws MessagingException {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for(String s : tokens){
            user = jwtService.getUser(s);
        }
        return UserRole.MODERATOR == user.getRole() && eventService.deleteEvent(id);
    }

}
