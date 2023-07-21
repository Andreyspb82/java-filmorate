package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public int delete(int id) {
        return reviewStorage.delete(id);
    }

    public List<Review> findAllReviews() {
        return reviewStorage.findAllReviews().stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Review findReviewById(int id) {
        return reviewStorage.findReviewById(id);
    }

    public List<Review> findReviewsByFilmId(int filmId, int count) {
        return reviewStorage.findReviewsByFilmId(filmId, count).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Review addLike(int id, int userId) {
        return reviewStorage.addLike(id, userId);
    }

    public Review addDislike(int id, int userId) {
        return reviewStorage.addDislike(id, userId);
    }

    public Review deleteLike(int id, int userId) {
        return reviewStorage.deleteLike(id, userId);
    }

    public Review deleteDislike(int id, int userId) {
        return reviewStorage.deleteDislike(id, userId);
    }
}
