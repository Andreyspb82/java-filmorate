package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public int delete(@Valid @PathVariable int id) {
        return reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@Valid @PathVariable int id) {
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public List<Review> findReviewsByFilmId(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10", required = false) @Positive int count) {
        if (filmId == null) {
            return reviewService.findAllReviews();
        }
        return reviewService.findReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@Valid @PathVariable int id,
                          @Valid @PathVariable int userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@Valid @PathVariable int id,
                             @Valid @PathVariable int userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(@Valid @PathVariable int id,
                             @Valid @PathVariable int userId) {
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(@Valid @PathVariable int id,
                                @Valid @PathVariable int userId) {
        return reviewService.deleteDislike(id, userId);
    }
}
