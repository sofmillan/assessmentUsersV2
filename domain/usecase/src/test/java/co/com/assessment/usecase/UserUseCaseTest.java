package co.com.assessment.usecase;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.gateway.IdentityProvider;
import co.com.assessment.model.gateway.UserPersistenceGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock
    private UserPersistenceGateway userPersistenceGateway;
    @Mock
    private IdentityProvider identityProvider;
    @InjectMocks
    private UserUseCase userUseCase;

    @Test
    void shouldSaveUserSuccessfully() {
        User baseUser = User.builder()
                .firstName("Noah")
                .lastName("Sebastian")
                .email("noah@gmail.com")
                .password("SecurePassword123?")
                .role("ROLE_HOST")
                .build();
        Mono<String> generatedId = Mono.just("30130c1d-c4a9-431b-8322-80fa999dbd97");
        when(identityProvider.registerUser(any(User.class))).thenReturn(generatedId);
        when(userPersistenceGateway.saveUser(any(User.class))).thenReturn(Mono.just(baseUser));

        userUseCase.registerUser(baseUser)
                .as(StepVerifier::create)
                .assertNext(createdUser ->{
                    assertNotNull(createdUser);
                    assertNotNull(createdUser.getId());
                    assertNotNull(createdUser.getRole());
                    assertEquals(baseUser.getEmail(), createdUser.getEmail());
                }).verifyComplete();

        verify(userPersistenceGateway).saveUser(any(User.class));
        verify(identityProvider).registerUser(any(User.class));
    }

    @Test
    void shouldAuthenticateUserSuccessfully(){
        User userLogin = User.builder()
                .email("noah@gmail.com")
                .password("Specter123")
                .build();

        Mono<AuthenticatedUser> authenticatedUser = Mono.just(AuthenticatedUser.builder()
                .type("Bearer")
                .expiresIn(3600)
                .refreshToken("sampleRefreshToken")
                .accessToken("sampleAccessToken")
                .build());

        when(identityProvider.authenticateUser(userLogin)).thenReturn(authenticatedUser);

        userUseCase.authenticateUser(userLogin)
                .as(StepVerifier::create)
                .assertNext(result ->{
                    assertNotNull(result);
                    assertNotNull(result.getAccessToken());
                    assertNotNull(result.getRefreshToken());
                    assertNotNull(result.getType());
                    assertNotNull(result.getExpiresIn());
                }).verifyComplete();

        verify(identityProvider).authenticateUser(any(User.class));
    }
}
