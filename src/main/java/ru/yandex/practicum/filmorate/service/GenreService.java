package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.List;

@Service
@Slf4j
@Data
public class GenreService {
    private final GenreDao genreDao;

    public Genre getGenreId(int id) {
        return genreDao.getGenreId(id);
    }

    public List<Genre> getGenres () {
        return genreDao.getGenres();
    }

}
