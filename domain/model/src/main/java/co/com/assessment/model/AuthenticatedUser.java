package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuthenticatedUser {
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String type;

}
