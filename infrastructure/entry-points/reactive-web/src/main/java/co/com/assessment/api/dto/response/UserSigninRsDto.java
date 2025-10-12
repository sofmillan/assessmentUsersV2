package co.com.assessment.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserSigninRsDto {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;
}
