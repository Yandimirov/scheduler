package ru.scheduler.messages.restcontroller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.dto.MessageDTO;
import ru.scheduler.messages.model.entity.Message;
import ru.scheduler.messages.service.ChatService;
import ru.scheduler.messages.service.MessageService;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.config.View;
import ru.scheduler.users.service.AuthService;
import ru.scheduler.users.service.JwtService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ChatController {
    @Value("${jwt.auth.header}")
    private String TOKEN_HEADER;

    @Autowired
    private AuthService authService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtService jwtService;

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "user/{idUser}/chats", method = RequestMethod.GET)
    public List<UserChat> chats(RequestEntity<?> request, @PathVariable Long idUser) {
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return chatService.findChats(user.getId());
    }

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "user/{idUser}/chats/{idChat}", method = RequestMethod.GET)
    public List<Message> messages(RequestEntity<?> request, @PathVariable long idUser, @PathVariable long idChat) {

        if (chatService.checkUserInChat(idUser, idChat)) {
            return messageService.findByChat(idChat);
        }
        return null;
    }

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "user/chats/{idChat}/{after}", method = RequestMethod.GET)
    public List<Message> messageAfter(RequestEntity<?> request, @PathVariable long idChat, @PathVariable long after){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        if(chatService.checkUserInChat(user.getId(), idChat)){
            return messageService.findByChatAfterDate(idChat, new Date(after));
        }
        return null;
    }

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "user/{idUser}/messages/", method = RequestMethod.POST)
    public Message postMessage(RequestEntity<MessageDTO> request, @PathVariable long idUser) {
        MessageDTO messageDTO = request.getBody();
        //System.out.println("Chat : " + messageDTO.getChatId() + " text : " + messageDTO);
        if (idUser != messageDTO.getRecipientId()) {
            return messageService.postMessage(idUser, messageDTO);
        }
        return null;
    }

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public Message postMessage2(RequestEntity<MessageDTO> request){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return messageService.postMessage2(request.getBody(), user);
    }

    @JsonView(View.MESSAGE.class)
    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public UserChat createChat(RequestEntity<ChatDTO> request){
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get(TOKEN_HEADER);
        User user = null;
        for (String s : tokens) {
            user = jwtService.getUser(s);
        }
        return chatService.createChat(request.getBody(), user);
    }
}
