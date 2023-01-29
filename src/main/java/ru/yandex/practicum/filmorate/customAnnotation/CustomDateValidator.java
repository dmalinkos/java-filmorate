package ru.yandex.practicum.filmorate.customAnnotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CustomDateValidator implements ConstraintValidator<IsAfterEarliestDate, LocalDate> {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(IsAfterEarliestDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
         return localDate.isAfter(EARLIEST_DATE);
    }
}
