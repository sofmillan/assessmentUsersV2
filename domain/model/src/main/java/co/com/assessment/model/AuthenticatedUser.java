package co.com.assessment.model;

import lombok.Data;

@Data
public class AuthenticatedUser {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;

}
