package ru.scheduler.events.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.EventType;

import java.util.Date;
import java.util.List;

/**
 * Created by Mikhail Yandimirov on 09.04.2017.
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByTypeNot(EventType type);
    List<Event> findByType(EventType type);
    List<Event> findByStartDateGreaterThanEqual(Date after);
    List<Event> findByStartDateGreaterThanEqualAndType(Date after, EventType type);

    @Query(value = "select * from eventes where date_part('month', start_date) = ?1", nativeQuery = true)
    List<Event> findByMonth(int month);

    @Query(value = "select * from eventes where date_part('month', start_date) = ?1 and type = 'APPROVED'", nativeQuery = true)
    List<Event> findApprovedEventsByMonth(int month);

    @Query(value = "select count(*) from events where info_id = ?1", nativeQuery = true)
    Long countByEventInfo(long infoId);
}