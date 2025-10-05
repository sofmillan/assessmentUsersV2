package co.com.assessment.cognito;

import co.com.assessment.cognito.model.CognitoCredentials;
import co.com.assessment.cognito.model.CognitoTokenResponse;
import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.TokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

@Log4j2
public class CognitoTokenProvider {
    public static final int COGNITO_RETRIES = 3;
    private final Mono<String> cachedToken;

    public CognitoTokenProvider(WebClient client, Mono<CognitoCredentials> credentialsProvider) {
        this.cachedToken = credentialsProvider
                .flatMap(credentials ->
                        client.post()
                                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                                        .with("client_id", credentials.getClientId())
                                        .with("client_secret", credentials.getClientSecret()))
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                                        .map(IOException::new))
                                .bodyToMono(CognitoTokenResponse.class)
                                .retry(COGNITO_RETRIES))
                .doOnError(err -> log.warn("Error calling cognito", err))
                .cache(this::cacheTime, error -> Duration.ZERO, () -> Duration.ZERO)
                .map(CognitoTokenResponse::getAccessToken);
        }


    public Mono<String> getToken() {
        return cachedToken;
    }


    public Mono<AuthenticatedUser> loginUser(Mono<User> user) {
        return null;
    }

    private Duration cacheTime(CognitoTokenResponse token) {
        log.info("Generated from cognito and expires in: {} seconds and will be refreshed in: {} seconds",
                token.getExpiresIn(), token.getCacheTime());
        return Duration.ofSeconds(token.getCacheTime());
    }
}
