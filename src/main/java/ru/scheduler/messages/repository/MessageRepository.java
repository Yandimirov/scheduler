package ru.scheduler.messages.repository;

import org.springframework.data.repository.CrudRepository;
import ru.scheduler.messages.model.entity.Chat;
import ru.scheduler.messages.model.entity.Message;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByChat(Chat chat);
    List<Message> findByChatAndTimeStampGreaterThan(Chat chat, Date date);
}

