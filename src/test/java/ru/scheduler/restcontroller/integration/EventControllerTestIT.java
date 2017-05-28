package ru.scheduler.restcontroller.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.scheduler.RestTemplateConfigurer;
import ru.scheduler.events.model.dto.EventDTO;
import ru.scheduler.events.model.dto.EventNotificationDTO;
import ru.scheduler.events.model.dto.PlaceDTO;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.model.entity.UserEvent;
import ru.scheduler.events.repository.EventInfoRepository;
import ru.scheduler.events.repository.EventRepository;
import ru.scheduler.events.repository.PlaceRepository;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EventControllerTestIT {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInfoRepository eventInfoRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    RestTemplate restTemplate;
    User user;
    String token;
    MultiValueMap<String, String> headers;
    Event eventForSubscirbe;

    @Before
    public void setUp() throws Exception {
        restTemplate = RestTemplateConfigurer.configure();

        user = new User();
        user.setEmail("test@mail.ru");
        user.setUsername("test");
        user.setImagePath("no-avatar.png");
        user.setCity("Санкт-Петербург");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(UserRole.MODERATOR);
        user = userRepository.save(user);

        token = jwtService.getToken(user);

        headers = new LinkedMultiValueMap<>();
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        headers.put(TOKEN_HEADER, tokens);

        Place place = Place.builder()
                .name("Subscribe Test")
                .id("TESTING")
                .lat(1.0)
                .lon(1.0)
                .build();
        place = placeRepository.save(place);

        EventInfo eventInfo = new EventInfo();
        eventInfo.setName("Subscribe TEST");
        eventInfo.setDescription("Subscribe TEST");
        eventInfo.setPlace(place);
        eventInfo = eventInfoRepository.save(eventInfo);

        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        Date date2 = calendar.getTime();

        eventForSubscirbe = new Event();
        eventForSubscirbe.setInfo(eventInfo);
        eventForSubscirbe.setStartDate(date1);
        eventForSubscirbe.setEndDate(date2);
        eventForSubscirbe = eventRepository.save(eventForSubscirbe);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(user);

        EventInfo eventInfo = eventForSubscirbe.getInfo();
        Place place = eventInfo.getPlace();

        eventRepository.delete(eventForSubscirbe);
        eventInfoRepository.delete(eventInfo);
        placeRepository.delete(place);
    }

    @Test
    public void subscribingAndUnSubscribingTest() throws Exception {
        List<Date> dates = new ArrayList<>();
        dates.add(new Date());
        EventNotificationDTO eventNotificationDTO = new EventNotificationDTO();
        eventNotificationDTO.setId(eventForSubscirbe.getId());
        eventNotificationDTO.setNotifications(dates);

        HttpEntity<EventNotificationDTO> entityForSubscribe = new HttpEntity<>(eventNotificationDTO, headers);

        ResponseEntity<UserEvent> response = restTemplate.exchange("https://localhost:8443/api/user/event/"+eventForSubscirbe.getId(), HttpMethod.POST, entityForSubscribe, new ParameterizedTypeReference<UserEvent>() {
        });

        UserEvent userEvent= response.getBody();
        Event subsribedEvent = userEvent.getEvent();

        assertThat(subsribedEvent.getId(), is(eventForSubscirbe.getId()));
        assertThat(subsribedEvent.getInfo().getId(), is(eventForSubscirbe.getInfo().getId()));

        User subsribedUser = userEvent.getUser();

        assertThat(subsribedUser.getId(), is(user.getId()));

        HttpEntity<?> entityForUsers = new HttpEntity<>(null, headers);

        ResponseEntity<List<User>> response1 = restTemplate.exchange("https://localhost:8443/api/event/"+eventForSubscirbe.getId()+"/users", HttpMethod.GET, entityForUsers, new ParameterizedTypeReference<List<User>>() {
        });

        List<User> users = response1.getBody();

        assertThat(users.size(), is(1));

        User user1 = users.get(0);

        assertThat(user1.getId(), is(user.getId()));

        HttpEntity<?> entityForUnsubscribe = new HttpEntity<>(null, headers);
        ResponseEntity<Boolean> response2 = restTemplate.exchange("https://localhost:8443/api/event/"+eventForSubscirbe.getId(), HttpMethod.DELETE, entityForUnsubscribe, new ParameterizedTypeReference<Boolean>() {
        });

        Boolean bool = response2.getBody();

        assertThat(bool, is(true));

        response1 = restTemplate.exchange("https://localhost:8443/api/event/"+eventForSubscirbe.getId()+"/users", HttpMethod.GET, entityForUsers, new ParameterizedTypeReference<List<User>>() {
        });

        List<User> users1 = response1.getBody();

        assertThat(users1.size(), is(0));
    }

    @Test
    public void addEventAndGetEventAndUpdateAndDeleteEvent() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        Date date2 = calendar.getTime();

        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId("Test");
        placeDTO.setName("TEST");
        placeDTO.setLat(1.0);
        placeDTO.setLng(1.0);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setName("Test");
        eventDTO.setDescription("TEST");
        eventDTO.setRepeats(null);
        eventDTO.setStartDate(date1);
        eventDTO.setEndDate(date2);
        eventDTO.setPlace(placeDTO);

        HttpEntity<EventDTO> entity = new HttpEntity<>(eventDTO, headers);

        ResponseEntity<List<Event>> response = restTemplate.exchange("https://localhost:8443/api/event", HttpMethod.POST, entity, new ParameterizedTypeReference<List<Event>>() {
        });

        List<Event> events = response.getBody();

        assertThat(events.size(), is(1));

        Event event = events.get(0);

        EventInfo eventInfo = event.getInfo();
        Place place = eventInfo.getPlace();

        assertThat(eventInfo.getName(), is(eventDTO.getName()));
        assertThat(eventInfo.getDescription(), is(eventDTO.getDescription()));

        assertThat(event.getStartDate(), is(eventDTO.getStartDate()));
        assertThat(event.getEndDate(), is(eventDTO.getEndDate()));

        assertThat(place.getName(), is(placeDTO.getName()));
        assertThat(place.getId(), is(placeDTO.getId()));
        assertThat(place.getLat(), is(placeDTO.getLat()));
        assertThat(place.getLon(), is(placeDTO.getLng()));

        Date date = new Date();

        event.setStartDate(date);

        HttpEntity<Event> entity1 = new HttpEntity<>(event, headers);

        ResponseEntity<Event> response1 = restTemplate.exchange("https://localhost:8443/api/event", HttpMethod.PUT, entity1, new ParameterizedTypeReference<Event>() {
        });

        Event updatedEvent = response1.getBody();

        assertThat(updatedEvent.getStartDate(), is(date));
        assertThat(updatedEvent.getEndDate(), is(event.getEndDate()));

        eventRepository.delete(updatedEvent);
        eventInfoRepository.delete(eventInfo);
        placeRepository.delete(place);
    }
}