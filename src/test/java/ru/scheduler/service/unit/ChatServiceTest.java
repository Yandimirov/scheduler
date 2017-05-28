package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.messages.converter.ChatConverter;
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.messages.repository.ChatRepository;
import ru.scheduler.messages.repository.UserChatRepository;
import ru.scheduler.users.repository.UserRepository;
import ru.scheduler.messages.service.ChatService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceTest {
    @Mock
    UserChatRepository userChatRepository;

    @Mock
    ChatRepository chatRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ChatConverter chatConverter;

    @InjectMocks
    ChatService chatService;

    User user;
    User user2;
    User user3;
    ChatDTO chatDTO;
    Chat chat;
    UserChat userChat;
    UserChat[] userChatsWithNullId = new UserChat[3];

    @Before
    public void before() {
        user = new User();
        user.setId(1L);
        user2 = new User();
        user2.setId(2L);
        user3 = new User();
        user3.setId(3L);
        chatDTO = new ChatDTO();
        chatDTO.setUsers(Arrays.asList(1L, 2L));
        chat = new Chat();
        chat.setId(1L);
        userChat = new UserChat();
        userChat.setId(1L);
        userChat.setUser(user);
        userChat.setChat(chat);

        userChatsWithNullId[0] = new UserChat();
        userChatsWithNullId[0].setChat(chat);
        userChatsWithNullId[0].setUser(user);

        userChatsWithNullId[1] = new UserChat();
        userChatsWithNullId[1].setChat(chat);
        userChatsWithNullId[1].setUser(user2);

        userChatsWithNullId[2] = new UserChat();
        userChatsWithNullId[2].setChat(chat);
        userChatsWithNullId[2].setUser(user3);
    }

    @Test
    public void checkUserInChat() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);
        when(userChatRepository.findByChatAndUser(chat, user)).thenReturn(new UserChat());
        chatService.checkUserInChat(user.getId(), chat.getId());
        verify(userRepository).findOne(user.getId());
        verify(chatRepository).findOne(chat.getId());
        verify(userChatRepository).findByChatAndUser(chat, user);
    }

    @Test
    public void findChats() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userChatRepository.findByUserAndOrderByLastMessage(user.getId())).thenReturn(Arrays.asList(userChat));
        when(userChatRepository.findByChatAndUserNot(chat, user)).thenReturn(Arrays.asList(userChat));
        chatService.findChats(user.getId());
        verify(userRepository).findOne(user.getId());
        verify(userChatRepository).findByUserAndOrderByLastMessage(user.getId());
        verify(userChatRepository).findByChatAndUserNot(chat, user);
    }

    @Test
    public void createChat() throws Exception {
        when(userRepository.findOne(user2.getId())).thenReturn(user2);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userChatRepository.findByUsersAndCountUsersEqualTwo(user.getId(), user2.getId())).thenReturn(chat.getId());
        when(chatConverter.convertChat(chatDTO)).thenReturn(chat);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userChatRepository.save(userChat)).thenReturn(userChat);
        when(userChatRepository.findByChatAndUser(chat, user)).thenReturn(userChat);
        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);

        chatService.createChat(chatDTO, user);

        verify(userRepository).findOne(user2.getId());
        verify(userChatRepository).findByUsersAndCountUsersEqualTwo(user.getId(), user2.getId());
        verify(chatRepository).findOne(chat.getId());
        verify(userChatRepository).findByChatAndUser(chat, user);
    }

    @Test
    public void createChat1() throws Exception {
        when(userRepository.findOne(user2.getId())).thenReturn(user2);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userChatRepository.findByUsersAndCountUsersEqualTwo(user.getId(), user2.getId())).thenReturn(null);
        when(chatConverter.convertChat(chatDTO)).thenReturn(chat);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userChatRepository.save(userChatsWithNullId[0])).thenReturn(userChat);
        when(userChatRepository.save(userChatsWithNullId[1])).thenReturn(userChat);
        when(userChatRepository.findByChatAndUser(chat, user)).thenReturn(userChat);
        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);

        chatService.createChat(chatDTO, user);
        verify(userRepository, times(2)).findOne(user2.getId());
        verify(chatConverter).convertChat(chatDTO);
        verify(chatRepository).save(chat);
        verify(userRepository).findOne(user.getId());
        verify(userChatRepository).save(userChatsWithNullId[0]);
        verify(userChatRepository).save(userChatsWithNullId[1]);
    }


    @Test
    public void createChat2() throws Exception {
        when(userRepository.findOne(user2.getId())).thenReturn(user2);
        when(userRepository.findOne(user3.getId())).thenReturn(user3);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userChatRepository.findByUsersAndCountUsersEqualTwo(user.getId(), user2.getId())).thenReturn(null);
        when(chatConverter.convertChat(chatDTO)).thenReturn(chat);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userChatRepository.save(userChatsWithNullId[0])).thenReturn(userChat);
        when(userChatRepository.save(userChatsWithNullId[1])).thenReturn(userChat);
        when(userChatRepository.save(userChatsWithNullId[2])).thenReturn(userChat);
        when(userChatRepository.findByChatAndUser(chat, user)).thenReturn(userChat);
        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatRepository.findOne(chat.getId())).thenReturn(chat);

        List<Long> users = new ArrayList<>();
        users.add(1L);
        users.add(2L);
        users.add(3L);
        chatDTO.setUsers(users);

        chatService.createChat(chatDTO, user);

        verify(chatConverter).convertChat(chatDTO);
        verify(chatRepository).save(chat);
        verify(userRepository).findOne(1L);
        verify(userChatRepository).save(userChatsWithNullId[0]);
        verify(userRepository).findOne(2L);
        verify(userChatRepository).save(userChatsWithNullId[1]);
        verify(userRepository).findOne(3L);
        verify(userChatRepository).save(userChatsWithNullId[2]);
        verify(userChatRepository).findByChatAndUser(chat, user);

    }

}