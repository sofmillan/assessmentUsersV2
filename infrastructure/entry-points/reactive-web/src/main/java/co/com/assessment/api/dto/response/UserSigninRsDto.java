package co.com.assessment.api.dto.response;

import lombok.Data;

@Data
public class UserSigninRsDto {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;
}
