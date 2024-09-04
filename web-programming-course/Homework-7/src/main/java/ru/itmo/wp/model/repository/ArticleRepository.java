package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;

import java.util.List;

public interface ArticleRepository {
    Article find(long id);

    List<Article> findAll();

    List<Article> findAllByUserId(long userId);

    void updateHidden(long id, boolean value);

    void save(Article article);
}
