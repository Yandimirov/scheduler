package ru.scheduler.events.model;

public interface CompositeIdentifiable<T extends CompositeId> {

    T getCompositeId();
}
