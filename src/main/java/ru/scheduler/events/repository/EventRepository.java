package ru.scheduler.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.Event.EventId;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mikhail Yandimirov on 09.04.2017.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, EventId>, CustomEventRepository {

    @Query(value = "select e from EVENTS e where e.compositeId.id = ?1 order by e.compositeId.version desc")
    List<Event> findAllVersionsById(long id);

    default Optional<Event> findLatestVersionById(long id) {
        return findAllVersionsById(id).stream()
                .max(Comparator.comparing(Event::getVersion));
    }

    List<Event> findByStartDateGreaterThanEqual(Date after);

    @Query(value = "select * from eventes where date_part('month', start_date) = ?1", nativeQuery = true)
    List<Event> findByMonth(int month);

    @Query(value = "select * from eventes where date_part('month', start_date) = ?1", nativeQuery = true)
    List<Event> findApprovedEventsByMonth(int month);

    @Query(value = "select count(*) from events where info_id = ?1", nativeQuery = true)
    Long countByEventInfo(long infoId);
}
