package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    int delete(int id);

    List<Review> findAllReviews();

    Review findReviewById(int id);

    List<Review> findReviewsByFilmId(int filmId, int count);

    Review addLike(int id, int userId);

    Review addDislike(int id, int userId);

    Review deleteLike(int id, int userId);

    Review deleteDislike(int id, int userId);
}
