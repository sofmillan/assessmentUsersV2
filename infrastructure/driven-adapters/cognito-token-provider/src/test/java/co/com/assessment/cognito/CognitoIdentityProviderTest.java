package co.com.assessment.cognito;

import co.com.assessment.model.User;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.exception.SecurityErrorMessage;
import co.com.assessment.model.exception.SecurityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CognitoIdentityProviderTest {
    @Mock
    private CognitoIdentityProviderAsyncClient cognitoClient;
    private CognitoService cognitoService;
    private static final String USER_POOL_ID = "test-user-pool-id";
    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private User user;

    @BeforeEach
    void setUp(){
        user = User.builder().email("noah@example.com").password("TestPassword124").build();
        cognitoService = new CognitoService(cognitoClient, USER_POOL_ID, CLIENT_ID, CLIENT_SECRET);
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        AuthenticationResultType authResult = AuthenticationResultType.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .expiresIn(3600)
                .tokenType("Bearer")
                .build();

        InitiateAuthResponse authResponse = InitiateAuthResponse.builder()
                .authenticationResult(authResult)
                .build();

        when(cognitoClient.initiateAuth(any(InitiateAuthRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(authResponse));

        cognitoService.authenticateUser(user)
                .as(StepVerifier::create)
                .assertNext(result ->{
                    assertNotNull(result);
                    assertEquals("mock-access-token",result.getAccessToken());
                    assertEquals("mock-refresh-token", result.getRefreshToken());
                    assertEquals(3600, result.getExpiresIn());
                    assertEquals("Bearer", result.getType());
                }).verifyComplete();

        verify(cognitoClient).initiateAuth(any(InitiateAuthRequest.class));
    }

    @Test
    void authenticateUserShouldThrowSecurityExceptionWhenInvalidCredentials() {
        when(cognitoClient.initiateAuth(any(InitiateAuthRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(NotAuthorizedException.builder().build()));

        cognitoService.authenticateUser(user)
                .as(StepVerifier::create)
                .verifyErrorSatisfies( exception ->{
                    assertTrue(exception instanceof SecurityException);
                    assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS.getMessage(),
                            exception.getMessage());
                });

        verify(cognitoClient).initiateAuth(any(InitiateAuthRequest.class));

    }
    @Test
    void authenticateUserShouldThrowSecurityExceptionWhenUserNotExists() {
        when(cognitoClient.initiateAuth(any(InitiateAuthRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(UserNotFoundException.builder().build()));

        cognitoService.authenticateUser(user)
                .as(StepVerifier::create)
                .verifyErrorSatisfies( exception ->{
                    assertTrue(exception instanceof SecurityException);
                    assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS.getMessage(),
                            exception.getMessage());
                });

        verify(cognitoClient).initiateAuth(any(InitiateAuthRequest.class));
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserType userType = UserType.builder().username(user.getEmail()).build();
        AdminCreateUserResponse createResponse = AdminCreateUserResponse.builder()
                .user(userType)
                .build();

        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(createResponse));

        when(cognitoClient.adminAddUserToGroup(any(AdminAddUserToGroupRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(AdminAddUserToGroupResponse.builder().build()));

        when(cognitoClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(AdminSetUserPasswordResponse.builder().build()));


        cognitoService.registerUser(user)
                .as(StepVerifier::create)
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();

        verify(cognitoClient).adminCreateUser(any(AdminCreateUserRequest.class));
        verify(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        verify(cognitoClient).adminSetUserPassword(any(AdminSetUserPasswordRequest.class));
    }

    @Test
    void registerUserShouldThrowBusinessExceptionWhenUserExists() {
        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(UsernameExistsException.builder().build()));

        cognitoService.registerUser(user)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED.getMessage(),
                            exception.getMessage());
                });

        verify(cognitoClient).adminCreateUser(any(AdminCreateUserRequest.class));
        verify(cognitoClient, never()).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        verify(cognitoClient, never()).adminSetUserPassword(any(AdminSetUserPasswordRequest.class));
    }

    @Test
    void calculateSecretHashShouldReturnHash() {
        String hash = CognitoService.calculateSecretHash(CLIENT_ID, CLIENT_SECRET, user.getEmail());

        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
}
