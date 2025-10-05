package co.com.assessment.usecase;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.PersistencePort;
import co.com.assessment.model.gateway.TokenProvider;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserSignupUseCase {
    private final PersistencePort persistencePort;
    private final TokenProvider tokenProvider;
    public Mono<User> registerUser(Mono<User> user){
        System.out.println("REGISTER USER USE CASE");
       return user
               .flatMap(user1 -> tokenProvider.registerUser(user1)
                   .map(id -> {
                       user1.setId(id);
                       return user1;
                   }))
               .flatMap(persistencePort::saveuSER);
    }

    public Mono<AuthenticatedUser> login(Mono<User> user){
        return user.flatMap(tokenProvider::loginUser);
    }
}
