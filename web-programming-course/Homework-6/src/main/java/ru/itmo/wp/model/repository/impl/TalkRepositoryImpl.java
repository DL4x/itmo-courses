package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Entity;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.TalkRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TalkRepositoryImpl extends BasicRepositoryImpl implements TalkRepository {
    @Override
    public List<Talk> findAllByUserId(long userId) {
        List<Talk> talks = new ArrayList<>();
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `Talk` WHERE (sourceUserId=? OR targetUserId=?) ORDER BY creationTime DESC")) {
                statement.setString(1, String.valueOf(userId));
                statement.setString(2, String.valueOf(userId));
                try (ResultSet resultSet = statement.executeQuery()) {
                    Talk talk;
                    while ((talk = toObject(statement.getMetaData(), resultSet)) != null) {
                        talks.add(talk);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find User.", e);
        }
        return talks;
    }

    @Override
    public void save(Talk talk) {
        save(talk, "(`sourceUserId`, `targetUserId`, `text`, `creationTime`)", "(?, ?, ?, NOW())");
    }

    @Override
    protected void setStatement(PreparedStatement statement, Entity obj, String... optional) throws SQLException {
        Talk talk = (Talk) obj;
        statement.setString(1, String.valueOf(talk.getSourceId()));
        statement.setString(2, String.valueOf(talk.getTargetId()));
        statement.setString(3, talk.getText());
    }

    @Override
    protected String tableName() {
        return Talk.class.getSimpleName();
    }

    @Override
    protected Talk toObject(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Talk talk = new Talk();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> talk.setId(resultSet.getLong(i));
                case "sourceUserId" -> talk.setSourceId(resultSet.getLong(i));
                case "targetUserId" -> talk.setTargetId(resultSet.getLong(i));
                case "text" -> talk.setText(resultSet.getString(i));
                case "creationTime" -> talk.setCreationTime(resultSet.getTimestamp(i));
                default -> {/* No operations. */}
            }
        }

        return talk;
    }
}
