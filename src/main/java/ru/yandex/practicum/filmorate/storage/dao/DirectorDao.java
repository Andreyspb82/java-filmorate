package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public Director addDirector( Director director){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(id.intValue());
        return director;
    }

    public Director updateDirector( Director director){
        String sqlUpdate = "update directors set name = ? WHERE id =?;";
       if(Objects.nonNull(getDirectorById(director.getId()))) {
       jdbcTemplate.update(sqlUpdate, director.getName(), director.getId());
       }
       return director;
    }

    public Director getDirectorById(int id){
        String sqlSelect = "select * from directors where id = ?;";
        try {
            return jdbcTemplate.queryForObject(sqlSelect, this::mapRowToDirector, id);
        } catch (DataAccessException e) {
            log.info("Режиссер с id = {} не найден", id );
            throw new NotFoundException("Режиссер с id = "+ id +" не найден");
        }
    }

    public List<Director> getAllDirectors(){
        String sqlSelect = "select * from directors ;";
        List<Director> directors = new ArrayList<>();
        directors =  jdbcTemplate.query(sqlSelect,this::mapRowToDirector);
        return directors;
    }

    public boolean deleteDirectorById(int id){
       String sqlDelete = "delete from directors where id = ?";
       return jdbcTemplate.update(sqlDelete,id) > 0;
    }

    private Director mapRowToDirector (ResultSet resultSet, int rowNum) throws SQLException {

        return new Director(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
