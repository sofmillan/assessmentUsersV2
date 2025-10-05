package co.com.assessment.cognito;

import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateway.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Service
public class CognitoService implements TokenProvider {
    private final CognitoIdentityProviderAsyncClient cognitoClient;
    private final String userPoolId;
    private final String clientId;
    private final String clientSecret;

    public CognitoService(
            @Value("${aws.cognito.userPoolId}") String userPoolId,
            @Value("${aws.cognito.clientId}") String clientId,
            @Value("${aws.cognito.region}") String region,
            @Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.cognito.clientSecret}") String clientSecret) {

        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.cognitoClient = CognitoIdentityProviderAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
    @Override
    public Mono<String> getToken() {
        return null;
    }

    @Override
    public Mono<String> registerUser(User user) {
        System.out.println("REGISTER USER COGNITO SERVICE");
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
                        Mono.zip(addtogroup(user.getEmail(), user.getRole()),
                                setPass(user.getEmail(), user.getPassword()))
                                .thenReturn(response.user().username()));

    }

    private Throwable handleCognitoExceptions(Throwable e) {
        if (e instanceof UsernameExistsException) {
            return new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED);
        }else if(e instanceof UserNotFoundException ){
            return new BusinessException(BusinessErrorMessage.INVALID_CREDENTIALS);
        }else if(e instanceof NotAuthorizedException){
            return new BusinessException(BusinessErrorMessage.INVALID_CREDENTIALS);
        }
        return e;
    }

    private Mono<Void> setPass(String email, String pass){
        System.out.println("SET PASS COGNITO SERVICE");
        AdminSetUserPasswordRequest setPasswordRequest = AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .password(pass)
                .permanent(true)
                .build();

        return Mono.fromFuture(cognitoClient.adminSetUserPassword(setPasswordRequest)).then();
    }

    private Mono<Void> addtogroup(String email, String role){
        System.out.println("ADD GROUP");
        AdminAddUserToGroupRequest addUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .groupName(role)
                .build();

        return Mono.fromFuture(cognitoClient.adminAddUserToGroup(addUserToGroupRequest)).then();
    }

    @Override
    public Mono<AuthenticatedUser> loginUser(User user) {

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
                .map(authResponse ->{
                    AuthenticationResultType result = authResponse.authenticationResult();
                    return   new AuthenticatedUser(result.accessToken(),
                            result.refreshToken(),
                            result.expiresIn(),
                            result.tokenType());
                });
    }


    public static String calculateSecretHash(String clientId, String clientSecret, String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating secret hash", e);
        }
    }
}
