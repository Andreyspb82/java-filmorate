package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;


@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;


    @Override
    public Film putFilm(Film film) {
        Set<Genre> genres = new HashSet<>(film.getGenres());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of(
                "name", film.getName(),
                "release_date", String.valueOf(film.getReleaseDate()),
                "description", film.getDescription(),
                "duration", String.valueOf(film.getDuration()),
                "rate", String.valueOf(film.getRate()),
                "mpa_id", String.valueOf(film.getMpa().getId())
        );
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());

        String sqlGenres = "insert into films_genres (film_id, genre_id) values (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(
                    sqlGenres,
                    film.getId(),
                    genre.getId()
            );
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        String sql = "select distinct  fg.genre_id, f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa,  g.name as name_genre from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id  " +
                "where f.id = ?;";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), film.getId());
        if (films.size() != 1) {
            log.warn("Фильма с Id = " + film.getId() + " нет");
            throw new NotFoundException("Фильма с Id = " + film.getId() + " нет");
        }


        int rateFilm = jdbcTemplate.queryForObject("select count(film_id) as rate from film_likes fl  " +
                        "where fl.film_id = ?;",
                (rs, rowNum) -> rs.getInt("rate"), film.getId());

        jdbcTemplate.update("delete from films_genres where film_id=?", film.getId());

        Set<Genre> genres = new HashSet<>(film.getGenres());

        String sqlFilm = "update films set name = ?, release_date = ?, description = ?,  duration = ?, rate = ? , mpa_id = ? where id = ?";
        jdbcTemplate.update(
                sqlFilm,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                rateFilm,
                film.getMpa().getId(),
                film.getId()
        );

        String sqlGenres = "insert into films_genres (film_id, genre_id) values (?, ?)";
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update(
                        sqlGenres,
                        film.getId(),
                        genre.getId()
                );
            }
        }
        return getFilmId(film.getId());

    }

    @Override
    public List<Film> getFilms() {
        try {
            String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                    "m.name as name_mpa, fg.genre_id,  g.name as name_genre from films f join mpa m on f.mpa_id = m.id  " +
                    "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                    "LEFT OUTER join  genres g on   fg.genre_id = g.id order by f.id ;";

            return jdbcTemplate.queryForObject(sql, filmsRowMapper());
        } catch (RuntimeException re) {
            return new ArrayList<>();
        }
    }


    @Override
    public Film getFilmId(int id) {
        String sql = "select distinct  fg.genre_id, f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa,  g.name as name_genre from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id  " +
                "where f.id = ?;";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), id);
        if (films.size() != 1) {
            log.warn("Фильма с Id = " + id + " нет");
            throw new NotFoundException("Фильма с Id = " + id + " нет");
        }
        return films.get(0);
    }


    @Override
    public void removeFilmId(int id) {
        jdbcTemplate.update("delete from films where id=?", id);
    }

    @Override
    public void addLikeFilm(int filmId, int userId) {
        String sgl = "insert into film_likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sgl, filmId, userId);
    }

    @Override
    public void removeLikeFilm(int filmId, int userId) {
        String sgl = "delete from film_likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(sgl, filmId, userId);
    }


    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setRate(rs.getInt("rate"));
            Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("name_mpa"));
            film.setMpa(mpa);
            if (rs.getString("name_genre") != null) {
                do {
                    Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name_genre"));
                    film.getGenres().add(genre);
                } while (rs.next());
            }
            return film;
        };
    }

    private RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            Film film = new Film();
            do {
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDescription(rs.getString("description"));
                film.setDuration(rs.getInt("duration"));
                film.setRate(rs.getInt("rate"));
                film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_mpa")));

                if (rs.getString("name_genre") != null) {
                    film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("name_genre")));
                }
                while (rs.next()) {
                    if (!film.getId().equals(rs.getInt("id"))) {
                        films.add(film);
                        film = new Film();
                        film.setId(rs.getInt("id"));
                        film.setName(rs.getString("name"));
                        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        film.setDescription(rs.getString("description"));
                        film.setDuration(rs.getInt("duration"));
                        film.setRate(rs.getInt("rate"));
                        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_mpa")));
                        if (rs.getString("name_genre") != null) {
                            film.getGenres().add(new Genre(rs.getInt("genre_id"),
                                    rs.getString("name_genre")));
                        }
                    } else {
                        if (rs.getString("name_genre") != null) {
                            film.getGenres().add(new Genre(rs.getInt("genre_id"),
                                    rs.getString("name_genre")));
                        }
                    }
                }
                films.add(film);
                film = new Film();
            } while (rs.next());
            return films;
        };
    }

}