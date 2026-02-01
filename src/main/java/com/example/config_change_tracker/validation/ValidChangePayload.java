package com.example.config_change_tracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChangePayloadValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidChangePayload {

    String message() default "Invalid before/after payload for changeType";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
