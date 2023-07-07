package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class GenreDao {

    private JdbcTemplate jdbcTemplate;

    public Genre getGenreId(int id) {
        String sql = "select * from genres where id=?";

        List<Genre> genres = jdbcTemplate.query(sql, mpaRowMapper(), id);
        if (genres.size() != 1) {
            log.warn("Жанра с таким Id нет");
            throw new NotFoundException("Жанра с таким Id нет");
        }
        return genres.get(0);
    }


    public List<Genre> getGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, mpaRowMapper());
    }

    private RowMapper<Genre> mpaRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        };
    }
}
