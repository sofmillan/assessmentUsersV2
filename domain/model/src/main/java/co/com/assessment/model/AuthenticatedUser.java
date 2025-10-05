package co.com.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUser {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;

}
