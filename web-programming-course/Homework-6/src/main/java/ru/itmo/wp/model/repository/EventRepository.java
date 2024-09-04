package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Entity;
import ru.itmo.wp.model.domain.Event;

public interface EventRepository {
    Entity find(long id);
    void save(Event event);
}
