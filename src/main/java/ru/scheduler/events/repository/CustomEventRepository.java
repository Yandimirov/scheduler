package ru.scheduler.events.repository;

import ru.scheduler.events.model.entity.Event;

public interface CustomEventRepository {

    Event persist(Event event);
}
