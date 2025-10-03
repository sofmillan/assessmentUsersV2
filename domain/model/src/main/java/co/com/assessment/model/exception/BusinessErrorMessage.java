package co.com.assessment.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {
    EMAIL_ALREADY_REGISTERED(409, "CONFLICT","Email is already registered");

    private final Integer statusCode;
    private final String title;
    private final String message;
}
