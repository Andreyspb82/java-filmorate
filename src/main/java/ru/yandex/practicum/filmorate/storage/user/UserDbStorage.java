package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final FilmStorage filmStorage;
    private final FeedDao feedDao;
    private JdbcTemplate jdbcTemplate;

    private static final Timestamp TEST_EVENT_TIME = Timestamp.from(Instant.ofEpochMilli(1670590017281L));

    @Override
    public User putUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("email", user.getEmail(), "login", user.getLogin(), "name", user.getName(),
                "birthday", String.valueOf(user.getBirthday()));
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        List<User> users = jdbcTemplate.query("select * from users where id = ?", userRowMapper(), user.getId());
        if (users.size() != 1) {
            log.warn("Пользователя с Id = " + user.getId() + " нет");
            throw new NotFoundException("Пользователя с Id = " + user.getId() + " нет");
        }
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("select * from users", userRowMapper());
    }

    @Override
    public User getUserId(int id) {
        List<User> users = jdbcTemplate.query("select * from users where id = ?", userRowMapper(), id);
        if (users.size() != 1) {
            log.warn("Пользователя с Id = " + id + " нет");
            throw new NotFoundException("Пользователя с Id = " + id + " нет");
        }
        return users.get(0);
    }

    @Override
    public void removeUserId(int id) {
        jdbcTemplate.update("delete from users where id=?", id);
    }


    @Override
    public void addFriendId(int userId, int friendId) {
        String sgl = "insert into friends (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(
                sgl,
                userId,
                friendId
        );

        feedDao.feedUser(userId, "FRIEND", "ADD", friendId);
    }

    @Override
    public List<User> getFreinds(int id) {
        return jdbcTemplate.query("select u.id , u.email , u.login , u.name , u.birthday " +
                " from users u join friends f on u.id = f.friend_id  where f.user_id =?", userRowMapper(), id);
    }

    @Override
    public void removeFriendId(int userId, int friendId) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?;", userId, friendId);

        feedDao.feedUser(userId, "FRIEND", "REMOVE", friendId);
    }


    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "select t1.id , t1.email , t1.login , t1.name , t1.birthday " +
                "from (select   u.id , u.email , u.login , u.name , u.birthday ,   f.friend_id from users u " +
                "join friends f on u.id = f.friend_id " +
                "where f.user_id = ?) as t1 " +
                "intersect " +
                "select t2.id , t2.email , t2.login , t2.name , t2.birthday " +
                "from (select   u.id , u.email , u.login , u.name , u.birthday ,   f.friend_id from users u " +
                "join friends f on u.id = f.friend_id " +
                "where f.user_id = ?) as t2";
        return jdbcTemplate.query(
                sql,
                userRowMapper(),
                userId,
                otherId);
    }

    @Override
    public List<Film> getFilmsRecommendations(int userId) {
        return filmStorage.getFilmsRecommendations(userId);
    }

    @Override
    public List<Feed> getFeedsId(int userId) {
        String sql = "select * from feed where user_Id = ?;";
        return jdbcTemplate.query(
                sql,
                feedRowMapper(),
                userId
        );
    }

    private RowMapper<Feed> feedRowMapper() {
        return (rs, rowNum) -> {
            Feed feed = new Feed();
            feed.setEventId(rs.getInt("event_id"));

            Timestamp timestamp = rs.getTimestamp("time_stamp");
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            Instant instant = localDateTime.toInstant(ZoneOffset.ofHours(0));
            Long time = instant.toEpochMilli();
            feed.setTimestamp(time);

            feed.setUserId(rs.getInt("user_id"));
            feed.setEventType(rs.getString("event_type"));
            feed.setOperation(rs.getString("operation"));
            feed.setEntityId(rs.getInt("entity_id"));
            return feed;
        };
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    @Override
    public void feedUser(int userId, String eventType, String operation, int entityId) {

        String sqlFeed = "insert into feed (time_stamp, user_id, event_type, operation, entity_id) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlFeed, TEST_EVENT_TIME, userId, eventType, operation, entityId);
    }

}
