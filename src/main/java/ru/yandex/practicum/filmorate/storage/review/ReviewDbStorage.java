package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedDao feedDao;

    @Override
    public Review create(Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw new NotFoundException("Такой id не существует");
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        review.setReviewId(insert.executeAndReturnKey(reviewToMap(review)).intValue());

        feedDao.feedUser(Timestamp.from(Instant.now()), review.getUserId(), EventType.REVIEW, Operation.ADD,
                review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE reviews " +
                "SET content=?, is_positive=?" +
                "WHERE id=? ";
        int result = jdbcTemplate.update(sqlQuery, review.getContent(), review.isPositive(), review.getReviewId());
        if (result == 0) {
            log.error("Отзыв для обновления с id = {} не найден", review.getReviewId());
            throw new NotFoundException("Отзыв для обновления с id = " + review.getReviewId() + " не найден");
        }
        Review reviewFeed = findReviewById(review.getReviewId());
        feedDao.feedUser(Timestamp.from(Instant.now()), reviewFeed.getUserId(), EventType.REVIEW, Operation.UPDATE,
                reviewFeed.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public int delete(int id) {
        Review review = findReviewById(id);
        feedDao.feedUser(Timestamp.from(Instant.now()), review.getUserId(), EventType.REVIEW, Operation.REMOVE,
                review.getReviewId());

        String sqlQuery = "DELETE " +
                "FROM reviews " +
                "WHERE reviews.id=? ";

        int result = jdbcTemplate.update(sqlQuery, id);
        if (result == 0) {
            log.error("Отзыв для удаления с id = {} не найден", id);
            throw new NotFoundException("Отзыв для удаления с id = " + id + " не найден");
        }
        return id;
    }

    @Override
    public List<Review> findAllReviews() {
        String sqlQuery = "SELECT * " +
                "FROM reviews";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview);
    }

    @Override
    public Review findReviewById(int id) {
        String sqlQuery = "SELECT *" +
                "FROM reviews " +
                "WHERE reviews.id=? ";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (DataAccessException e) {
            log.error("Отзыв с id = {} не найден", id);
            throw new NotFoundException("Отзыв с id " + id + " не найден");
        }
    }

    @Override
    public List<Review> findReviewsByFilmId(int filmId, int count) {
        String sqlQuery = "SELECT * " +
                "FROM reviews " +
                "WHERE film_id=? " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public Review addLike(int id, int userId) {
        if (entityIsExists(id, userId, 1)) {
            log.error("Отзыв для добавления лайка с id = {} уже существует", id);
            return findReviewById(id);
        }
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, type) VALUES (?, ?, 1)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return findReviewById(id);
    }

    @Override
    public Review addDislike(int id, int userId) {
        if (entityIsExists(id, userId, -1)) {
            log.error("Отзыв для добавления дизлайка с id = {} уже существует", id);
            return findReviewById(id);
        }
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, type) VALUES (?, ?, -1)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return findReviewById(id);
    }

    @Override
    public Review deleteLike(int id, int userId) {
        if (!entityIsExists(id, userId, 1)) {
            log.error("Отзыв для удаления лайка с id = {} не найден", id);
            throw new NotFoundException("Отзыв для удаления лайка с id = " + id + " не найден");
        }
        String sqlQuery = "DELETE " +
                "FROM like_review " +
                "WHERE review_id=? AND user_id=? AND type=1";
        jdbcTemplate.update(sqlQuery, id, userId);
        return findReviewById(id);
    }

    @Override
    public Review deleteDislike(int id, int userId) {
        if (!entityIsExists(id, userId, -1)) {
            log.error("Отзыв для удаления дизлайка с id = {} не найден", id);
            throw new NotFoundException("Отзыв для удаления дизлайка с id = " + id + " не найден");
        }
        String sqlQuery = "DELETE " +
                "FROM like_review " +
                "WHERE review_id=? AND user_id=? AND type=-1";
        jdbcTemplate.update(sqlQuery, id, userId);
        return findReviewById(id);
    }

    private boolean entityIsExists(int id, int userId, int type) {
        String sqlQuery = "SELECT review_id " +
                "FROM like_review " +
                "WHERE review_id=? AND user_id=? AND type=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, id, userId, type);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private Map<String, Object> reviewToMap(Review review) {
        return Map.of(
                "content", review.getContent(),
                "is_positive", review.isPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId()
        );
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(getUseful(rs.getInt("id")))
                .build();
    }

    private Integer getUseful(int review_id) {
        String sqlQuery = "SELECT sum(type) " +
                "FROM like_review " +
                "WHERE review_id=? ";
        Integer likes = jdbcTemplate.queryForObject(sqlQuery, Integer.class, review_id);
        if (likes == null) {
            return 0;
        }
        return likes;
    }
}
