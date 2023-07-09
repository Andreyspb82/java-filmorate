package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor

public class MpaDao {

    private JdbcTemplate jdbcTemplate;

    public Mpa getMpaId(int id) {
        String sql = "select * from mpa where id=?";

        List<Mpa> mpas = jdbcTemplate.query(sql, mpaRowMapper(), id);
        if (mpas.size() != 1) {
            log.warn("MPA с Id = " + id + " нет");
            throw new NotFoundException("MPA с Id = " + id + " нет");
        }
        return mpas.get(0);
    }


    public List<Mpa> getMpas() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, mpaRowMapper());
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        };
    }

}
