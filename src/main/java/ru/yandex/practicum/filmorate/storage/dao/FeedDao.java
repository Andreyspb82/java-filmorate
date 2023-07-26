package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.Timestamp;

@Component
@Slf4j
@AllArgsConstructor
public class FeedDao {

    private final JdbcTemplate jdbcTemplate;

    public void feedUser(Timestamp timestamp, int userId, EventType eventType, Operation operation, int entityId) {

        String sqlFeed = "insert into feed (time_stamp, user_id, event_type, operation, entity_id) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlFeed, timestamp, userId, String.valueOf(eventType), String.valueOf(operation), entityId);
    }

}
