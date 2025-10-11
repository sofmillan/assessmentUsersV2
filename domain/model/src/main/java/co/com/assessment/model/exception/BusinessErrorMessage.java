package co.com.assessment.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {
    INVALID_REQUEST(400, "Bad Request", "Body cannot be empty"),
    INVALID_CREDENTIALS(401, "Unauthorized","Invalid credentials"),
    EMAIL_ALREADY_REGISTERED(409, "Conflict","Email is already registered");

    private final Integer statusCode;
    private final String status;
    private final String message;
}
