package co.com.assessment.usecase;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.UserPersistenceGateway;
import co.com.assessment.model.gateway.IdentityProvider;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {
    private final UserPersistenceGateway userPersistenceGateway;
    private final IdentityProvider identityProvider;
    public Mono<User> registerUser(User user){
        return identityProvider.registerUser(user)
                .flatMap(id ->{
                    user.setId(id);
                    return userPersistenceGateway.saveUser(user);
                });
    }

    public Mono<AuthenticatedUser> authenticateUser(User user){
        return identityProvider.authenticateUser(user);
    }
}
