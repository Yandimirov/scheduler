package ru.scheduler.messages.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.messages.converter.ChatConverter;
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.messages.repository.ChatRepository;
import ru.scheduler.messages.repository.UserChatRepository;
import ru.scheduler.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private UserChatRepository userChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatConverter chatConverter;

    public Boolean checkUserInChat(Long idUser, Long idChat){
        User user = userRepository.findOne(idUser);
        Chat chat = chatRepository.findOne(idChat);
        UserChat userChat = userChatRepository.findByChatAndUser(chat, user);
        return (userChat != null);
    }

    public List<UserChat> findChats(Long idUser){
        User user = userRepository.findOne(idUser);
        List<UserChat> userChats = userChatRepository.findByUserAndOrderByLastMessage(idUser);
        List<UserChat> filteredChats = new ArrayList<>();
        for (UserChat userChat : userChats) {
            List<UserChat> filteredChat = userChatRepository.findByChatAndUserNot(userChat.getChat(), user);
            filteredChats.add(filteredChat.get(0));
        }
        return filteredChats;
    }

    public UserChat createChat(ChatDTO chatDTO, User user){
        List<Long> users = chatDTO.getUsers();
        Chat chat = null;
        if(users.size() == 2){
            User recipient = null;
            for(Long id : users){
                if(!id.equals(user.getId())){
                    recipient = userRepository.findOne(id);
                    break;
                }
            }
            Long chatId = userChatRepository.findByUsersAndCountUsersEqualTwo(user.getId(), recipient.getId());
            if(chatId == null){
                chat = chatConverter.convertChat(chatDTO);
                chat.setName(null);
                chat = chatRepository.save(chat);
                for(Long id : users){
                    UserChat userChat = new UserChat();
                    userChat.setUser(userRepository.findOne(id));
                    userChat.setChat(chat);
                    userChatRepository.save(userChat);
                }
            } else {
                chat = chatRepository.findOne(chatId);
            }
        }
        else{
            chat = chatRepository.save(chatConverter.convertChat(chatDTO));
            for(Long id : users){
                UserChat userChat = new UserChat();
                userChat.setChat(chat);
                userChat.setUser(userRepository.findOne(id));
                userChatRepository.save(userChat);
            }
        }
        return userChatRepository.findByChatAndUser(chat, user);
    }
}
