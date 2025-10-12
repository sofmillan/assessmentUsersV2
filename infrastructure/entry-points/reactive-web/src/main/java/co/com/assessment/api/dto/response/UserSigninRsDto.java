package co.com.assessment.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserSigninRsDto {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;
}
