package co.com.assessment.usecase;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.UserRepository;
import co.com.assessment.model.gateway.IdentityProvider;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final IdentityProvider identityProvider;
    public Mono<User> registerUser(Mono<User> user){
       return user
               .flatMap(user1 -> identityProvider.registerUser(user1)
                   .map(id -> {
                       user1.setId(id);
                       return user1;
                   }))
               .flatMap(userRepository::saveuSER);
    }

    public Mono<AuthenticatedUser> login(Mono<User> user){
        return user.flatMap(identityProvider::loginUser);
    }
}
