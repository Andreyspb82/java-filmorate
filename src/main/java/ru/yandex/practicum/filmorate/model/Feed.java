package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    @PositiveOrZero
    private int eventId;

    @PositiveOrZero
    private int userId;

    @PositiveOrZero
    private int entityId;

    @NotBlank
    private String eventType;

    @NotBlank
    private String operation;

    @NotNull
    private Long timestamp;
}
