package co.com.assessment.model.gateway;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();
}