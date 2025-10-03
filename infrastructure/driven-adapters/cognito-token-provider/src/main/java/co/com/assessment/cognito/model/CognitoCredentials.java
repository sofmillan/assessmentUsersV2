package co.com.assessment.cognito.model;

import lombok.Data;

@Data
public class CognitoCredentials {
    private String clientId;
    private String clientSecret;
}
