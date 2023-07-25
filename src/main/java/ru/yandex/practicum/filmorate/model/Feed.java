package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Feed {

    private int eventId;

    private int userId;

    private int entityId;

    private String eventType;

    private String operation;

    private Long timestamp;

    public Feed() {
    }

}
