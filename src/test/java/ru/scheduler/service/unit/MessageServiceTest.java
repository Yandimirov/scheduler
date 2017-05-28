package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.messages.model.dto.MessageDTO;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.messages.model.entity.Message;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.repository.ChatRepository;
import ru.scheduler.messages.repository.MessageRepository;
import ru.scheduler.messages.repository.UserChatRepository;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.messages.service.MessageService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    ChatRepository chatRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserChatRepository userChatRepository;

    @InjectMocks
    MessageService messageService;

    Chat chat;
    Date date;
    MessageDTO messageDTO;
    User user;
    Message message;

    @Before
    public void before(){
        chat = new Chat();
        chat.setId(1L);
        date = Calendar.getInstance().getTime();
        user = new User();
        user.setId(1L);
        messageDTO = new MessageDTO();
        messageDTO.setChatId(chat.getId());
        messageDTO.setTimeStamp(date);
        messageDTO.setText("123");
        message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setText(messageDTO.getText());
        message.setTimeStamp(messageDTO.getTimeStamp());
    }

    @Test
    public void findByChat() throws Exception {
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);
        when(messageRepository.findByChat(chat)).thenReturn(new ArrayList<Message>());

        messageService.findByChat(chat.getId());

        verify(chatRepository).findOne(chat.getId());
        verify(messageRepository).findByChat(chat);
    }

    @Test
    public void findByChatAfterDate() throws Exception {
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);
        when(messageRepository.findByChatAndTimeStampGreaterThan(chat, date)).thenReturn(new ArrayList<Message>());

        messageService.findByChatAfterDate(chat.getId(), date);

        verify(chatRepository).findOne(chat.getId());
        verify(messageRepository).findByChatAndTimeStampGreaterThan(chat, date);
    }

    @Test
    public void postMessage2() throws Exception {
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);
        when(messageRepository.save(message)).thenReturn(message);

        messageService.postMessage2(messageDTO, user);

        verify(chatRepository).findOne(chat.getId());
        verify(messageRepository).save(message);
    }

    @Test
    public void postMessage() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(chatRepository.findOne(messageDTO.getChatId())).thenReturn(chat);
        when(messageRepository.save(message)).thenReturn(message);

        messageService.postMessage(user.getId(), messageDTO);

        verify(userRepository).findOne(user.getId());
        verify(chatRepository).findOne(messageDTO.getChatId());
        verify(messageRepository).save(message);
    }

}