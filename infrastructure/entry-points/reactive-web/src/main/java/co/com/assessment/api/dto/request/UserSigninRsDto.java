package co.com.assessment.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSigninRsDto {
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required")
    private String password;
}
