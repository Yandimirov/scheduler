package ru.scheduler.messages.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.messages.model.entity.MessageInfo;

@Repository
public interface MessageInfoRepository extends CrudRepository<MessageInfo, Long> {
}
