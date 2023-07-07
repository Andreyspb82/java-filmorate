package ru.yandex.practicum.filmorate.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;

@Service
@Slf4j
@Data
public class MpaService {
    private final MpaDao mpaDao;

    public Mpa getMpaId(int id) {
        return mpaDao.getMpaId(id);
    }

    public List<Mpa> getMpas () {
        return mpaDao.getMpas ();
    }

}
