package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Entity;
import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.repository.EventRepository;

import java.sql.*;

@SuppressWarnings("SqlNoDataSourceInspection")
public class EventRepositoryImpl extends BasicRepositoryImpl implements EventRepository {
    @Override
    public void save(Event event) {
        save(event, "(`userId`, `type`, `creationTime`)", "(?, ?, NOW())");
    }

    @Override
    protected void setStatement(PreparedStatement statement, Entity obj, String... optional) throws SQLException {
        Event event = (Event) obj;
        statement.setString(1, String.valueOf(event.getUserId()));
        statement.setString(2, String.valueOf(event.getType()));
    }

    @Override
    protected String tableName() {
        return Event.class.getSimpleName();
    }

    @Override
    protected Event toObject(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Event event = new Event();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> event.setId(resultSet.getLong(i));
                case "userId" -> event.setUserId(resultSet.getLong(i));
                case "type" -> event.setType(Enum.valueOf(Event.Type.class, resultSet.getString(i)));
                case "creationTime" -> event.setCreationTime(resultSet.getTimestamp(i));
                default -> { /* No operations. */ }
            }
        }

        return event;
    }
}
