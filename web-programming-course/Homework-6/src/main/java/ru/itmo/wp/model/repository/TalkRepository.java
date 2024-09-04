package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Entity;
import ru.itmo.wp.model.domain.Talk;

import java.util.List;

public interface TalkRepository {
    Entity find(long id);
    List<Talk> findAllByUserId(long userId);
    void save(Talk talk);
}
