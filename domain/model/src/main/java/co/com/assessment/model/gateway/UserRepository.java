package co.com.assessment.model.gateway;

import co.com.assessment.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveuSER(User user);
}
