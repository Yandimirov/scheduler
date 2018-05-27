package ru.scheduler.events.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.model.entity.UserEvent;

public class EventRepositoryImpl implements CustomEventRepository {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserEventRepository userEventRepository;

    @Override
    @Transactional
    public Event persist(Event event) {
        if (event.getCompositeId() != null && event.getCompositeId().getId() != 0) {
            List<Event> foundByIdEvents = entityManager
                    .createQuery("select e from EVENTS e where e.compositeId.id = :id", Event.class)
                    .setParameter("id", event.getId())
                    .getResultList();

            Optional<Event> foundLastVersion = foundByIdEvents.stream()
                    .max(Comparator.comparing(Event::getVersion));

            if (foundLastVersion.isPresent()) {
                if (equals(foundLastVersion.get(), event)) {
                    return foundLastVersion.get();
                } else {
                    event.getCompositeId().setVersion(foundLastVersion.get().getVersion() + 1);
                    entityManager.persist(event);
                    userEventRepository.findByEvent(foundLastVersion.get()).stream()
                            .map(userEvent -> new UserEvent(userEvent.getId(), event, userEvent.getUser(), userEvent.getNotifications(), userEvent.getStatus()))
                            .forEach(userEventRepository::save);
                    return event;
                }
            }
        }

        entityManager.persist(event);

        return event;
    }

    private static boolean equals(Event first, Event second) {
        if (first == second) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        return first.getId() == second.getId()
                && Objects.equals(first.getInfo(), second.getInfo())
                && second.getStartDate().getTime() == first.getStartDate().getTime()
                && second.getStartDate().getTime() == first.getStartDate().getTime()
                && first.getType() == second.getType();
    }
}
