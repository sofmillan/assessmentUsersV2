package co.com.assessment.model.gateway;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();

    Mono<AuthenticatedUser> loginUser(User user);
    Mono<String> registerUser(User user);
}