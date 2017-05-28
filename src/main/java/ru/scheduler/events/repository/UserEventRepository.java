package ru.scheduler.events.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.events.model.entity.UserEvent;

import java.util.List;

/**
 * Created by Mikhail Yandimirov on 09.04.2017.
 */

@Repository
public interface UserEventRepository extends CrudRepository<UserEvent, Long> {
    UserEvent findByEventAndUser(Event event, User user);
    List<UserEvent> findByEvent(Event event);
    List<UserEvent> findByUser(User user);
}
