package co.com.assessment.cognito;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.exception.SecurityErrorMessage;
import co.com.assessment.model.exception.SecurityException;
import co.com.assessment.model.gateway.IdentityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Service
public class CognitoService implements IdentityProvider {
    private final CognitoIdentityProviderAsyncClient cognitoClient;

    private  final String userPoolId;
    private final String clientId;

    private  final String clientSecret;

    public CognitoService(
            CognitoIdentityProviderAsyncClient cognitoClient,
            @Value("${aws.cognito.userPoolId}") String userPoolId,
            @Value("${aws.cognito.clientId}") String clientId,
            @Value("${aws.cognito.clientSecret}") String clientSecret) {

        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.cognitoClient = cognitoClient;

    }

    @Override
    public Mono<AuthenticatedUser> authenticateUser(User user) {
        String secretHash = calculateSecretHash(clientId, clientSecret, user.getEmail());

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(Map.of(
                        "USERNAME", user.getEmail(),
                        "PASSWORD", user.getPassword(),
                        "SECRET_HASH", secretHash
                ))
                .build();

        return Mono.fromFuture(cognitoClient.initiateAuth(authRequest))
                .onErrorMap(this::handleCognitoExceptions)
                .map(authResponse ->{
                    AuthenticationResultType result = authResponse.authenticationResult();
                    return   AuthenticatedUser.builder()
                            .accessToken(result.accessToken())
                            .refreshToken(result.refreshToken())
                            .expiresIn(result.expiresIn())
                            .type(result.tokenType())
                            .build();
                });
    }

    @Override
    public Mono<String> registerUser(User user) {
        String secretHash = calculateSecretHash(clientId, clientSecret, user.getEmail());

        AdminCreateUserRequest signUpRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(user.getEmail())
                .temporaryPassword(user.getPassword())
                .userAttributes(
                        AttributeType.builder().name("email").value(user.getEmail()).build()
                )
                .messageAction("SUPPRESS")
                .clientMetadata(Map.of("SECRET_HASH", secretHash))

                .build();
        Mono<AdminCreateUserResponse> registrationResult = Mono.fromFuture(cognitoClient.adminCreateUser(signUpRequest));


        return registrationResult.onErrorMap(this::handleCognitoExceptions)
                .flatMap(response ->
                        Mono.zip(addUserToGroup(user.getEmail(), user.getRole()),
                                setPermanentPassword(user.getEmail(), user.getPassword()))
                                .thenReturn(response.user().username()));

    }

    private Mono<Void> setPermanentPassword(String email, String password){
        AdminSetUserPasswordRequest setPasswordRequest = AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .password(password)
                .permanent(true)
                .build();

        return Mono.fromFuture(cognitoClient.adminSetUserPassword(setPasswordRequest)).then();
    }

    private Mono<Void> addUserToGroup(String email, String role){
        AdminAddUserToGroupRequest addUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .groupName(role)
                .build();

        return Mono.fromFuture(cognitoClient.adminAddUserToGroup(addUserToGroupRequest)).then();
    }


    private Throwable handleCognitoExceptions(Throwable e) {
        if (e instanceof UsernameExistsException) {
            return new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED);
        }else if(e instanceof UserNotFoundException ){
            return new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        }else if(e instanceof NotAuthorizedException){
            return new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        }
        return e;
    }
    public static String calculateSecretHash(String clientId, String clientSecret, String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculating secret hash", e);
        }
    }
}
