package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class IsAfterValidator implements ConstraintValidator<IsAfter, LocalDate> {
    private LocalDate date;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        date = LocalDate.of(constraintAnnotation.year(), constraintAnnotation.month(), constraintAnnotation.day());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.isAfter(date);
    }
}
