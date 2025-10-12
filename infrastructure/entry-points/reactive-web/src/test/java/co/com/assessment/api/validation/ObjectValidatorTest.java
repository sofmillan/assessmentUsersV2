package co.com.assessment.api.validation;

import co.com.assessment.api.dto.request.UserSignupRqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectValidatorTest {
    private ObjectValidator objectValidator;
    @BeforeEach
    void setUp() {
        objectValidator = new ObjectValidator();
    }

    @Test
    void validateShouldNotThrowExceptionWhenObjectIsValid() {
        UserSignupRqDto validUser = UserSignupRqDto.builder()
                .email("noah@example.com")
                .password("StrongPassWooord123*")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_USER")
                .build();

        assertDoesNotThrow(() -> objectValidator.validate(validUser));
    }

    @Test
    void validateShouldThrowObjectValidationExceptionWhenRequiredFieldNotPresent() {
        UserSignupRqDto userWithoutEmail = UserSignupRqDto.builder()
                .password("StrongPassWooord123*")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_USER")
                .build();

        ObjectValidationException ex = assertThrows(
                ObjectValidationException.class,
                () -> objectValidator.validate(userWithoutEmail)
        );

        assertTrue(ex.getDetails().contains("email is required"));
    }

    @Test
    void validateShouldCollectMultipleErrorMessagesWhenMultipleViolations() {
        UserSignupRqDto invalidUser = UserSignupRqDto.builder()
                .password("St13*")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_PLAYER")
                .build();

        ObjectValidationException ex = assertThrows(
                ObjectValidationException.class,
                () -> objectValidator.validate(invalidUser)
        );
        assertFalse(ex.getDetails().isEmpty());
        assertTrue( ex.getDetails().size()>1);
    }
}
