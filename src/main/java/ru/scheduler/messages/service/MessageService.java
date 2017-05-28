package ru.scheduler.messages.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.messages.model.dto.MessageDTO;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.messages.model.entity.Message;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.messages.model.entity.UserChat;
import ru.scheduler.messages.repository.ChatRepository;
import ru.scheduler.messages.repository.MessageRepository;
import ru.scheduler.messages.repository.UserChatRepository;
import ru.scheduler.users.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserChatRepository userChatRepository;

    public List<Message> findByChat(long idChat){
        Chat chat = chatRepository.findOne(idChat);
        return messageRepository.findByChat(chat);
    }

    public List<Message> findByChatAfterDate(long id, Date after){
        Chat chat = chatRepository.findOne(id);
        return messageRepository.findByChatAndTimeStampGreaterThan(chat, after);
    }

    public Message postMessage2(MessageDTO messageDTO, User user) {
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setTimeStamp(messageDTO.getTimeStamp());
        message.setSender(user);
        if (messageDTO.getChatId() != null) {
            Chat chat = chatRepository.findOne(messageDTO.getChatId());
            if(chat != null){
                message.setChat(chat);
            }
        }
        return messageRepository.save(message);
    }

    public Message postMessage(long from, MessageDTO messageDTO ){
        User sender = userRepository.findOne(from);
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setTimeStamp(messageDTO.getTimeStamp());
        message.setSender(sender);
        UserChat filteredChat = null;
        if(messageDTO.getChatId() != null){
            Chat chat = chatRepository.findOne(messageDTO.getChatId());
            if(chat != null){
                message.setChat(chat);
                return messageRepository.save(message);
            }
        }
        User recipient = userRepository.findOne(messageDTO.getRecipientId());

        List<UserChat> userChats = userChatRepository.findByUser(sender);
        for (UserChat userChat : userChats) {
            filteredChat = userChatRepository.findByChatAndUser(userChat.getChat(), recipient);
            if(filteredChat != null) {
                long countUsers = userChatRepository.countByUsersInChat(filteredChat.getChat().getId());
                if(countUsers < 3){
                    break;
                } else {
                    filteredChat = null;
                }
            }
        }
        if(filteredChat != null){
            message.setChat(filteredChat.getChat());
        } else {
            Chat chat = chatRepository.save(new Chat());
            UserChat first = new UserChat();
            first.setChat(chat);
            first.setUser(sender);
            userChatRepository.save(first);
            UserChat second = new UserChat();
            second.setUser(recipient);
            second.setChat(chat);
            userChatRepository.save(second);
            message.setChat(chat);
        }
        return messageRepository.save(message);
    }
}
