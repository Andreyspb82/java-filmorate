package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {

        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {

        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id){
        return directorService.getDirectorById(id);
    }

    @GetMapping
    public List<Director> getAllDirectors(){
        return directorService.getAllDirectors();
    }
    @DeleteMapping("/{id}")
    public boolean deleteDirectorById(@PathVariable int id){
        return directorService.deleteDirectorById(id);
    }

}
