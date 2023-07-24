package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@Slf4j
@AllArgsConstructor
public class FeedDao {

    private final JdbcTemplate jdbcTemplate;

    private static final Timestamp EVENT_TIME = Timestamp.from(Instant.now());


    public void feedUser(int userId, String eventType, String operation, int entityId) {

        String sqlFeed = "insert into feed (time_stamp, user_id, event_type, operation, entity_id) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlFeed, EVENT_TIME, userId, eventType, operation, entityId);
    }


}
