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
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.users.model.entity.UserRole;
import ru.scheduler.messages.repository.ChatRepository;
import ru.scheduler.messages.repository.UserChatRepository;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.messages.service.ChatService;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.messages.service.MessageService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatControllerTestIT {
    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserChatRepository userChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtService jwtService;

    MultiValueMap<String, String> headers1;
    MultiValueMap<String, String> headers2;
    RestTemplate restTemplate;
    String token1;
    String token2;
    User user1;
    User user2;
    ChatDTO chatDTO;
    List<Long> userIDs;

    @Before
    public void setUp() throws Exception {
        restTemplate = RestTemplateConfigurer.configure();

        user1 = new User();
        user1.setEmail("test1@mail.ru");
        user1.setUsername("test1");
        user1.setImagePath("no-avatar.png");
        user1.setCity("Санкт-Петербург");
        user1.setFirstName("Test1");
        user1.setLastName("Test1");
        user1.setRole(UserRole.MODERATOR);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setEmail("test2@mail.ru");
        user2.setUsername("test2");
        user2.setImagePath("no-avatar.png");
        user2.setCity("Санкт-Петербург");
        user2.setFirstName("Test2");
        user2.setLastName("Test2");
        user2.setRole(UserRole.USER);
        user2 = userRepository.save(user2);

        userIDs = new ArrayList<>();
        userIDs.add(user1.getId());
        userIDs.add(user2.getId());

        chatDTO = new ChatDTO();
        chatDTO.setUsers(userIDs);

        token1 = jwtService.getToken(user1);
        token2 = jwtService.getToken(user2);

        headers1 = new LinkedMultiValueMap<>();
        List<String> tokens = new ArrayList<>();
        tokens.add(token1);
        headers1.put(TOKEN_HEADER, tokens);

        headers2 = new LinkedMultiValueMap<>();
        tokens = new ArrayList<>();
        tokens.add(token2);
        headers2.put(TOKEN_HEADER, tokens);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    public void createChatAndGetChats() throws Exception {
        HttpEntity<ChatDTO> entity = new HttpEntity<>(chatDTO, headers1);

        ResponseEntity<UserChat> response1 = restTemplate.exchange("https://localhost:8443/api/chat/", HttpMethod.POST, entity, new ParameterizedTypeReference<UserChat>() {
        });

        UserChat userChat1 = response1.getBody();

        User testUser1 = userChat1.getUser();

        assertThat(testUser1.getEmail(), is(user1.getEmail()));
        assertThat(testUser1.getId(), is(user1.getId()));
        assertThat(testUser1.getFirstName(), is(user1.getFirstName()));
        assertThat(testUser1.getLastName(), is(user1.getLastName()));

        HttpEntity<?> entity1 = new HttpEntity<>(null, headers2);

        ResponseEntity<List<UserChat>> response2 = restTemplate.exchange("https://localhost:8443/api/user/"+user2.getId()+"/chats", HttpMethod.GET, entity1, new ParameterizedTypeReference<List<UserChat>>() {
        });

        List<UserChat> userChats2 = response2.getBody();

        assertThat(userChats2.size(), is(1));

        UserChat userChat2 = userChats2.get(0);

        User testUser2 = userChat2.getUser();

        assertThat(testUser2.getEmail(), is(user1.getEmail()));
        assertThat(testUser2.getId(), is(user1.getId()));
        assertThat(testUser2.getFirstName(), is(user1.getFirstName()));
        assertThat(testUser2.getLastName(), is(user1.getLastName()));

        assertThat(userChat1.getChat(), is(userChat2.getChat()));


        HttpEntity<?> entity2 = new HttpEntity<>(null, headers1);

        ResponseEntity<List<UserChat>> response3 = restTemplate.exchange("https://localhost:8443/api/user/"+user1.getId()+"/chats", HttpMethod.GET, entity2, new ParameterizedTypeReference<List<UserChat>>() {
        });

        List<UserChat> userChats3 = response3.getBody();

        assertThat(userChats3.size(), is(1));

        UserChat userChat3 = userChats3.get(0);

        User testUser3 = userChat3.getUser();

        assertThat(testUser3.getEmail(), is(user2.getEmail()));
        assertThat(testUser3.getId(), is(user2.getId()));
        assertThat(testUser3.getFirstName(), is(user2.getFirstName()));
        assertThat(testUser3.getLastName(), is(user2.getLastName()));

        assertThat(userChat2.getChat(), is(userChat3.getChat()));

        Chat chat = userChat2.getChat();

        userChatRepository.delete(userChat2);
        userChatRepository.delete(userChat3);
        chatRepository.delete(chat);
    }
}
