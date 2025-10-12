package co.com.assessment.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserSignupRqDto {
    @NotNull(message = "email is required")
    @Email(message = "email must have a valid format")
    private String email;

    @NotNull(message = "password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\d])(?=.*[\\^$*.\\[\\]{}\\(\\)?\\-\\\"!@#%&\\/,><\\':;|_~`])\\S{8,99}$", message = "password is not strong enough")
    private String password;

    @NotNull(message = "firstName is required")
    @Size(min = 1, max=50, message = "firstName must be between 1 and 50 characters long")
    private String firstName;

    @NotNull(message = "lastName is required")
    @Size(min = 1, max=50, message = "lastName must be between 1 and 50 characters long")
    private String lastName;

    @NotNull(message = "role is required")
    @Pattern(regexp = "ROLE_HOST|ROLE_USER", message = "role is not valid")
    private String role;
}
