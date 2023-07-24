package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
public class Feed {

    @PositiveOrZero
    private int eventId;

    private int userId;

    private int entityId;

    private String eventType;

    private String operation;

    private Long timestamp;

    public Feed() {
    }


}
