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
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTestIT {

    RestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;


    User user;
    String token;
    MultiValueMap<String, String> headers;
    List<User> users;

    @Before
    public void setUp() throws Exception {
        restTemplate = RestTemplateConfigurer.configure();

        Date date = Calendar.getInstance().getTime();

        user = new User();
        user.setEmail("test@mail.ru");
        user.setUsername("test");
        user.setImagePath("no-avatar.png");
        user.setBirthday(date);
        user.setCity("Санкт-Петербург");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(UserRole.MODERATOR);
        user = userRepository.save(user);

        users = userService.fidnAllByEmailNot(user.getEmail());

        token = jwtService.getToken(user);

        headers = new LinkedMultiValueMap<>();
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        headers.put(TOKEN_HEADER, tokens);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(user);
    }

    @Test
    public void user() throws Exception {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<User> response = restTemplate.exchange("https://localhost:8443/api/user/"+user.getId(), HttpMethod.GET, entity, new ParameterizedTypeReference<User>() {
        });

        User testUser = response.getBody();

        assertThat(testUser.getId(), is(user.getId()));
        assertThat(testUser.getFirstName(), is(user.getFirstName()));
        assertThat(testUser.getLastName(), is(user.getLastName()));
        assertThat(testUser.getCity(), is(user.getCity()));
        assertThat(testUser.getEmail(), is(user.getEmail()));
    }

    @Test
    public void getRole() throws Exception {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<UserRole> response = restTemplate.exchange("https://localhost:8443/api/users/"+user.getId()+"/role", HttpMethod.GET, entity, new ParameterizedTypeReference<UserRole>() {
        });

        UserRole testRole = response.getBody();

        assertThat(testRole, is(user.getRole()));
    }

    @Test
    public void getAllUsers() throws Exception {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<User>> response = restTemplate.exchange("https://localhost:8443/api/users/", HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
        });

        List<User> testsUsers = response.getBody();

        assertThat(testsUsers.size(), is(users.size()));
    }

}