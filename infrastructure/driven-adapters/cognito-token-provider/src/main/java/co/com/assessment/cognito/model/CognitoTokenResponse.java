package co.com.assessment.cognito.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CognitoTokenResponse {
    public static final int FREE_REFRESH_SECONDS = 60; // 1 minute before expiration
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("token_type")
    private String tokenType;

    public int getCacheTime() {
        return getExpiresIn() - FREE_REFRESH_SECONDS;
    }
}
