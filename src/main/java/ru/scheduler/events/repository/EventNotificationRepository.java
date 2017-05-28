package ru.scheduler.events.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.EventNotification;
import ru.scheduler.events.model.entity.UserEvent;

import java.util.Date;
import java.util.List;


/**
 * Created by Mikhail Yandimirov on 09.04.2017.
 */
@Repository
public interface EventNotificationRepository extends CrudRepository<EventNotification, Long> {
    List<EventNotification> findByEvent(UserEvent event);
    List<EventNotification> findByWhenGreaterThanEqualAndWhenLessThanEqual(Date after, Date before);
}
