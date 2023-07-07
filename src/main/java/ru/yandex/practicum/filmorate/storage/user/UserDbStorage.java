package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Component("userDbStorage")
//@Qualifier("userDbStorage")
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;


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
    }

    @Override
    public List<User> getFreinds(int id) {
        return jdbcTemplate.query("select u.id , u.email , u.login , u.name , u.birthday " +
                " from users u join friends f on u.id = f.friend_id  where f.user_id =?", userRowMapper(), id);
    }

    @Override
    public void removeFriendId(int userId, int friendId) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?;", userId, friendId);
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


    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

}
