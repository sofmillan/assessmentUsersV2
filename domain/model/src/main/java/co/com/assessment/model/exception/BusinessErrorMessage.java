package co.com.assessment.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {
    EMAIL_ALREADY_REGISTERED(409, "CONFLICT","Email is already registered"),
    INVALID_REQUEST(400, "BAD REQUEST", "Body cannot be empty"),
    INVALID_CREDENTIALS(401, "Unauthorized","Invalid credentials");

    private final Integer statusCode;
    private final String title;
    private final String message;
}
