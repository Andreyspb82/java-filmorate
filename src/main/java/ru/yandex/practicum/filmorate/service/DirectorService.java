package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

import java.util.List;

@Service
@Data
@AllArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    public Director getDirectorById(int id) {
        return directorDao.getDirectorById(id);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public boolean deleteDirectorById(int id) {
        return directorDao.deleteDirectorById(id);
    }
}
