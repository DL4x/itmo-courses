package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Entity;
import ru.itmo.wp.model.domain.User;

import java.util.List;

public interface UserRepository {
    Entity find(long id);

    User findByLogin(String login);

    User findByLoginAndPasswordSha(String login, String passwordSha);

    User findByEmail(String email);

    List<User> findAll();

    void save(User user, String passwordSha);
}
