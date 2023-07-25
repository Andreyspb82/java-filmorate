package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;
    private final FeedDao feedDao;

    @Override
    public Film putFilm(Film film) {
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

        List<Genre> genres = new ArrayList<>(new HashSet<>(film.getGenres()));

        if (!genres.isEmpty()) {
            StringBuilder sqlBuilder = new StringBuilder("insert into films_genres (film_id, genre_id) values ");
            for (int i = 0; i < genres.size(); i++) {
                sqlBuilder.append("(" + film.getId() + ", " + genres.get(i).getId() + ")");
                if ((i < (genres.size() - 1))) {
                    sqlBuilder.append(",");
                }
            }
            String sqlGenres = sqlBuilder.toString();
            jdbcTemplate.update(sqlGenres);
        }
        addDirectors(film);
        return film;
    }

    private void addDirectors(Film film) { // Добавляет режиссеров из фильма в таблицу films_directors
        List<Director> directors = film.getDirectors().stream().distinct().collect(Collectors.toList());
        if (!directors.isEmpty()) {
            StringBuilder sqlBuilder = new StringBuilder("insert into films_directors (film_id, director_id) values ");
            for (int i = 0; i < directors.size(); i++) {
                sqlBuilder.append("(" + film.getId() + ", " + directors.get(i).getId() + ")");
                if ((i < (directors.size() - 1))) {
                    sqlBuilder.append(",");
                }
            }
            String sqlDirector = sqlBuilder.toString();
            jdbcTemplate.update(sqlDirector);
        }
    }


    @Override
    public Film updateFilm(Film film) {

        String sql = "select distinct  fg.genre_id, f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id  from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id  " +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id  " +
                "LEFT OUTER join directors d on fd.director_id = d.id  " +
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

        String sqlFilm = "update films set name = ?, release_date = ?, description = ?,  duration = ?, rate = ?, " +
                "mpa_id = ? where id = ?";
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

        List<Genre> genres = new ArrayList<>(new HashSet<>(film.getGenres()));

        if (!genres.isEmpty()) {
            StringBuilder sqlBuilder = new StringBuilder("insert into films_genres (film_id, genre_id) values ");
            for (int i = 0; i < genres.size(); i++) {
                sqlBuilder.append("(" + film.getId() + ", " + genres.get(i).getId() + ")");
                if ((i < (genres.size() - 1))) {
                    sqlBuilder.append(",");
                }
            }
            String sqlGenres = sqlBuilder.toString();
            jdbcTemplate.update(sqlGenres);
        }
        jdbcTemplate.update("delete from FILMS_DIRECTORS where film_id=?", film.getId());
        addDirectors(film);
        return getFilmId(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, t1.count_likes as rate,  f.mpa_id,  \n" +
                "                m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id \n" +
                "from films f join mpa m on f.mpa_id = m.id\n" +
                "LEFT OUTER join (select count (fl.user_id) as count_likes, film_id from    film_likes fl GROUP by film_id order by film_id) as t1 on f.id = t1.film_id\n" +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id\n" +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id \n" +
                "LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id \n" +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id order by f.id, fg.genre_id ;";
        List<List<Film>> films = jdbcTemplate.query(sql, filmsRowMapper());
        if (films.size() == 1) {
            return films.get(0);
        } else {
            log.warn("Список фильмов пустой");
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> getFilmsByGenreAndYear(Optional<Integer> genreId, Optional<Integer> year, int count) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id, " +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre,  d.name as director_name, fd.DIRECTOR_ID as director_id from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id " +
                "LEFT OUTER join directors d on fd.director_id = d.id  " +
                "WHERE f.id = (SELECT fg.film_id FROM films_genres fg WHERE fg.genre_id = ?) AND EXTRACT (YEAR FROM CAST (f.release_date AS DATE)) = ? " +
                "ORDER BY f.rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper(), genreId.get(), year.get(), count);
    }

    @Override
    public List<Film> getFilmsByGenre(Optional<Integer> genreId, int count) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id " +
                "LEFT OUTER join directors d on fd.director_id = d.id  " +
                "WHERE f.id = (SELECT fg.film_id FROM films_genres fg WHERE fg.genre_id = ?) " +
                "ORDER BY f.rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper(), genreId.get(), count);
    }

    @Override
    public List<Film> getFilmsByYear(Optional<Integer> year, int count) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id " +
                "LEFT OUTER join directors d on fd.director_id = d.id  " +
                "WHERE EXTRACT (YEAR FROM CAST (f.release_date AS DATE)) = ? " +
                "ORDER BY f.rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper(), year.get(), count);
    }

    @Override
    public List<Film> getFilmsByUserId(int userId) {
        String sqlQuery = "SELECT f.id, f.name, f.release_date, f.description, f.duration, f.rate, f.mpa_id,\n" +
                "                m.name AS name_mpa, fg.genre_id, g.name AS name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id,\n" +
                "                COUNT(fl.user_id) AS num_likes\n" +
                "                FROM films f\n" +
                "                JOIN mpa m ON f.mpa_id = m.id\n" +
                "                LEFT JOIN films_genres fg ON f.id = fg.film_id\n" +
                "                LEFT JOIN genres g ON fg.genre_id = g.id\n" +
                "                LEFT JOIN film_likes fl ON f.id = fl.film_id\n" +
                "                LEFT OUTER join films_directors fd on f.id = fd.film_id\n" +
                "                LEFT OUTER join directors d on fd.director_id = d.id\n" +
                "                WHERE fl.user_id = ?\n" +
                "                GROUP BY f.id, f.name, f.release_date, f.description, f.duration, f.rate, f.mpa_id, m.name, fg.genre_id, g.name\n" +
                "                ORDER BY num_likes DESC;";
        return jdbcTemplate.query(sqlQuery, filmRowMapper(), userId);
    }

    @Override
    public Film getFilmId(int id) {
        String sql = "select distinct  fg.genre_id, f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id,  " +
                "m.name as name_mpa,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id from films f join mpa m on f.mpa_id = m.id  " +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id  " +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id  " +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id  " +
                "LEFT OUTER join directors d on fd.director_id = d.id  " +
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
        feedDao.feedUser(Timestamp.from(Instant.now()), userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @Override
    public void removeLikeFilm(int filmId, int userId) {
        String sgl = "delete from film_likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(sgl, filmId, userId);
        feedDao.feedUser(Timestamp.from(Instant.now()), userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    @Override
    public List<Film> getFilmsRecommendations(int userId) {

        String sql = "select t3.id,\n" +
                "       t3.name,\n" +
                "       t3.release_date,\n" +
                "       t3.description,\n" +
                "       t3.duration,\n" +
                "       t3.rate,\n" +
                "       t3.mpa_id,\n" +
                "       m.name as name_mpa,\n" +
                "       fg.genre_id,\n" +
                "       g.name as name_genre,\n" +
                "       d.name as director_name,\n" +
                "       fd.DIRECTOR_ID as director_id\n" +
                "from (select *\n" +
                "      from (select f.id,\n" +
                "                   f.name,\n" +
                "                   f.release_date,\n" +
                "                   f.description,\n" +
                "                   f.duration,\n" +
                "                   f.rate,\n" +
                "                   f.mpa_id\n" +
                "            from films f\n" +
                "                     join film_likes fl on\n" +
                "                f.id = fl.film_id\n" +
                "            where fl.user_id in (select t4.user_id\n" +
                "                                 from (select (count(f.id)) as count_film,\n" +
                "                                              fl.user_id\n" +
                "                                       from films f\n" +
                "                                                join film_likes fl on f.id = fl.film_id\n" +
                "                                       where f.id in (select f.id\n" +
                "                                                      from films f\n" +
                "                                                               join film_likes fl on f.id = fl.film_id\n" +
                "                                                      where fl.user_id = ?)\n" +
                "                                         and not fl.user_id = ?\n" +
                "                                       GROUP by fl.user_id\n" +
                "                                       order by count_film desc\n" +
                "                                       limit 1) as t4)) as t2\n" +
                "      except\n" +
                "      select *\n" +
                "      from (select f.id, f.name, f.release_date, f.description, f.duration, f.rate, f.mpa_id\n" +
                "            from films f\n" +
                "                     join film_likes fl on f.id = fl.film_id\n" +
                "            where fl.user_id = ?) as t1) as t3\n" +
                "         join mpa m on t3.mpa_id = m.id\n" +
                "         LEFT OUTER join films_genres fg on t3.id = fg.film_id\n" +
                "         LEFT OUTER join genres g on fg.genre_id = g.id\n" +
                "         LEFT OUTER join films_directors fd on t3.id = fd.film_id\n" +
                "         LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id\n" +
                "order by fg.genre_id;";

        List<List<Film>> films = jdbcTemplate.query(sql, filmsRowMapper(), userId, userId, userId);
        if (films.size() == 1) {
            return films.get(0);
        } else {
            log.warn("Список фильмов пустой");
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, t1.count_likes as rate,  f.mpa_id,  \n" +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id \n" +
                "from films f join mpa m on f.mpa_id = m.id\n" +
                "LEFT OUTER join (select count (fl.user_id) as count_likes, film_id from film_likes fl GROUP by film_id order by film_id) as t1 on f.id = t1.film_id\n" +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id\n" +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id \n" +
                "LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id \n" +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "WHERE lower(f.name) LIKE lower(?) or lower(d.name) LIKE lower(?)";

        return jdbcTemplate.query(sql, filmRowMapper(), "%" + query + "%", "%" + query + "%");
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, t1.count_likes as rate,  f.mpa_id,  \n" +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id \n" +
                "from films f join mpa m on f.mpa_id = m.id\n" +
                "LEFT OUTER join (select count (fl.user_id) as count_likes, film_id from film_likes fl GROUP by film_id order by film_id) as t1 on f.id = t1.film_id\n" +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id\n" +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id \n" +
                "LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id \n" +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "WHERE lower(d.name) LIKE lower(?)";

        return jdbcTemplate.query(sql, filmRowMapper(), "%" + query + "%");
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        String sql = "select  f.id, f.name,  f.release_date, f.description, f.duration, t1.count_likes as rate,  f.mpa_id,  \n" +
                "m.name as name_mpa, fg.genre_id,  g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id \n" +
                "from films f join mpa m on f.mpa_id = m.id\n" +
                "LEFT OUTER join (select count (fl.user_id) as count_likes, film_id from film_likes fl GROUP by film_id order by film_id) as t1 on f.id = t1.film_id\n" +
                "LEFT OUTER join films_genres fg on f.id = fg.film_id\n" +
                "LEFT OUTER join films_directors fd on f.id = fd.film_id \n" +
                "LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id \n" +
                "LEFT OUTER join  genres g on   fg.genre_id = g.id " +
                "WHERE lower(f.name) LIKE lower(?)";

        return jdbcTemplate.query(sql, filmRowMapper(), "%" + query + "%");
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

            if (rs.getString("director_name") != null || rs.getString("name_genre") != null) {
                do {
                    if (rs.getString("director_name") != null) {
                        Director director = new Director(rs.getInt("director_id"), rs.getString("director_name"));
                        film.getDirectors().add(director);
                    }
                    if (rs.getString("name_genre") != null) {
                        Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name_genre"));
                        film.getGenres().add(genre);
                    }
                } while (rs.next());
            }

            return film;
        };
    }

    private RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            do {
                Film film = new Film();
                setFilmProperties(rs, film);
                fillGenres(rs, film);
                fillDirectors(rs, film);
                while (rs.next()) {
                    if (!film.getId().equals(rs.getInt("id"))) {
                        films.add(film);
                        film = new Film();
                        setFilmProperties(rs, film);
                        fillGenres(rs, film);
                        fillDirectors(rs, film);
                    } else {
                        fillGenres(rs, film);
                        fillDirectors(rs, film);
                    }
                }
                films.add(film);
            } while (rs.next());
            return films;
        };
    }

    private void fillGenres(ResultSet rs, Film film) throws SQLException {
        if (rs.getString("name_genre") != null) {
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name_genre"));
            if (!film.getGenres().contains(genre)) {
                film.getGenres().add(genre);
            }
        }
    }

    private void fillDirectors(ResultSet rs, Film film) throws SQLException {
        if (rs.getString("director_name") != null) {
            Director director = new Director(rs.getInt("director_id"), rs.getString("director_name"));
            if (!film.getDirectors().contains(director)) {
                film.getDirectors().add(director);
            }
        }

    }

    private void setFilmProperties(ResultSet rs, Film film) throws SQLException {
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_mpa")));
    }

    @Override
    public List<Film> getFilmsByDirector(int id, String sort) {
        if (sort.equals("year")) {
            return getSortedFilms(id, "release_date");
        } else if (sort.equals("likes")) {
            return getSortedFilms(id, "rate DESC");
        } else {
            return getSortedFilms(id, "f.id");
        }
    }

    private List<Film> getSortedFilms(int id, String sort) {
        List<Film> films = new ArrayList<>();
        String sqlSelect = "select f.id, f.name,  f.release_date, f.description, f.duration, f.rate, f.mpa_id, m.name as name_mpa, fg.genre_id,\n" +
                "       g.name as name_genre, d.name as director_name, fd.DIRECTOR_ID as director_id from films f join mpa m on f.mpa_id = m.id\n" +
                "       LEFT OUTER join films_genres fg on f.id = fg.film_id\n" +
                "       LEFT OUTER join films_directors fd on f.id = fd.film_id\n" +
                "       LEFT OUTER join directors d on fd.DIRECTOR_ID = d.id\n" +
                "       LEFT OUTER join  genres g on   fg.genre_id = g.id\n" +
                "where DIRECTOR_ID =?\n" +
                "ORDER BY " + sort;

        List<List<Film>> listsFilms = jdbcTemplate.query(sqlSelect, filmsRowMapper(), id);
        if (!listsFilms.isEmpty()) {
            return listsFilms.get(0);
        }
        throw new NotFoundException("Нет режиссера с id=" + id);

    }
}

