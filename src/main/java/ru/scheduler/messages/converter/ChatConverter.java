package ru.scheduler.messages.converter;

import org.springframework.stereotype.Component;
import ru.scheduler.messages.model.dto.ChatDTO;
import ru.scheduler.messages.model.entity.Chat;

/**
 * Created by Mikhail Yandimirov on 29.04.2017.
 */

@Component
public class ChatConverter {
    public Chat convertChat(ChatDTO chatDTO){
        Chat chat = new Chat();
        chat.setName(chatDTO.getName());
        chat.setPicture("no-avatar.png");
        return chat;
    }
}
