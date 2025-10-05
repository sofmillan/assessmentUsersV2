package co.com.assessment.usecase;

import co.com.assessment.model.User;
import co.com.assessment.model.gateway.PersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserSignupUseCase {
    private final PersistencePort persistencePort;
    public Mono<User> registerUser(Mono<User> user){
       return user
               .doOnNext(c->c.setId(UUID.randomUUID().toString()))
               .flatMap(persistencePort::saveuSER);
    }
}
