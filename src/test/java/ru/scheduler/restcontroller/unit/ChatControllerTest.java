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
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.dto.MessageDTO;
import ru.scheduler.messages.model.entity.Message;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.messages.restcontroller.ChatController;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.messages.service.ChatService;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.messages.service.MessageService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mikhail Yandimirov on 13.05.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChatControllerTest {
    @Mock
    AuthService authService;

    @Mock
    ChatService chatService;

    @Mock
    MessageService messageService;

    @Mock
    JwtService jwtService;

    @InjectMocks
    ChatController chatController;

    private String TOKEN_HEADER;

    String token;
    User user;
    Long idUser;
    Long idChat;
    List<UserChat> chats;
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    Date date;
    MessageDTO messageDTO;
    ChatDTO chatDTO;

    @Before
    public void before()
    {
        token = "123";
        idUser = 1L;
        idChat = 1L;
        user = new User();
        user.setToken(token);
        user.setId(idUser);
        chats = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        TOKEN_HEADER = "x-auth-token";
        date = Calendar.getInstance().getTime();
        headers.put(TOKEN_HEADER, tokens);
        ReflectionTestUtils.setField(chatController, "TOKEN_HEADER", TOKEN_HEADER);
        messageDTO = new MessageDTO();
        chatDTO = new ChatDTO();
    }

    @Test
    public void chats() throws Exception {
        when(jwtService.getUser(token)).thenReturn(user);
        when(chatService.findChats(idUser)).thenReturn(new ArrayList<UserChat>());

        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);

        chatController.chats(request, idUser);
        verify(jwtService).getUser(token);
        verify(chatService).findChats(idUser);
    }

    @Test
    public void messages() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);
        when(authService.checkUser(request.getHeaders(), idUser)).thenReturn(true);
        when(chatService.checkUserInChat(idUser, idChat)).thenReturn(true);
        when(messageService.findByChat(idChat)).thenReturn(new ArrayList<Message>());
        chatController.messages(request, idUser, idChat);
        verify(authService).checkUser(request.getHeaders(), idUser);
        verify(chatService).checkUserInChat(idUser, idChat);
        verify(messageService).findByChat(idChat);
    }

    @Test
    public void messageAfter() throws Exception {
        RequestEntity<?> request = new RequestEntity<Object>("", headers, null, null);

        when(jwtService.getUser(token)).thenReturn(user);
        when(chatService.checkUserInChat(idUser, idChat)).thenReturn(true);
        when(messageService.findByChatAfterDate(idChat, date)).thenReturn(new ArrayList<Message>());
        chatController.messageAfter(request, idChat, date.getTime());
        verify(jwtService).getUser(token);
        verify(chatService).checkUserInChat(idUser, idChat);
        verify(messageService).findByChatAfterDate(idUser, date);
    }

    @Test
    public void postMessage() throws Exception {
        RequestEntity<MessageDTO> request = new RequestEntity<>(messageDTO, headers, null, null);

        when(authService.checkUser(request.getHeaders(), idUser)).thenReturn(true);
        when(messageService.postMessage(idUser, messageDTO)).thenReturn(new Message());
        chatController.postMessage(request, idUser);
        verify(authService).checkUser(request.getHeaders(), idUser);
        verify(messageService).postMessage(idUser, messageDTO);

    }

    @Test
    public void postMessage2() throws Exception {
        RequestEntity<MessageDTO> request = new RequestEntity<>(messageDTO, headers, null, null);

        when(jwtService.getUser(token)).thenReturn(user);
        when(messageService.postMessage2(messageDTO, user)).thenReturn(new Message());
        chatController.postMessage2(request);
        verify(jwtService).getUser(token);
        verify(messageService).postMessage2(messageDTO, user);
    }

    @Test
    public void createChat() throws Exception {
        RequestEntity<ChatDTO> request = new RequestEntity<>(chatDTO, headers, null, null);
        when(jwtService.getUser(token)).thenReturn(user);
        when(chatService.createChat(chatDTO, user)).thenReturn(new UserChat());
        chatController.createChat(request);
        verify(jwtService).getUser(token);
        verify(chatService).createChat(chatDTO, user);
    }

}