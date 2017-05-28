package ru.scheduler.messages.repository;

import org.springframework.data.repository.CrudRepository;
import ru.scheduler.messages.model.entity.Chat;

public interface ChatRepository extends CrudRepository<Chat, Long>{
}
