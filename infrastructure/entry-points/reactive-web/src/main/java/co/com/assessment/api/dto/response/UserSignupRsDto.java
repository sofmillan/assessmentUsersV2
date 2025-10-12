package co.com.assessment.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserSignupRsDto {
    private String firstName;
    private String lastName;
    private String email;
}
