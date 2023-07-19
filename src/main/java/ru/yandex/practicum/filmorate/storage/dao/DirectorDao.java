package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

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
        return null;
    }

    public Director getDirectorById(int id){
        return null;
    }

    public ArrayList<Director> getAllDirectors(){
        return null;
    }

    public boolean deleteDirectorById(int id){
        return true;
    }
}
