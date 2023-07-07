package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@AllArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getMpas() {
        return mpaService.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpaId(@PathVariable int id) {
        return mpaService.getMpaId(id);
    }

}
