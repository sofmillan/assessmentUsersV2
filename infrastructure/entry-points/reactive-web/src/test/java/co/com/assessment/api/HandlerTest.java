package co.com.assessment.api;

import co.com.assessment.api.dto.request.UserSigninRqDto;
import co.com.assessment.api.dto.request.UserSignupRqDto;
import co.com.assessment.api.dto.response.UserSigninRsDto;
import co.com.assessment.api.dto.response.UserSignupRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.AuthenticatedUser;
import co.com.assessment.model.User;
import co.com.assessment.usecase.UserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class HandlerTest {
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private ObjectValidator objectValidator;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private Handler handler;

    @Test
    void listenPOSTUserSignupShouldReturnOkWhenValidRequest() {
        UserSignupRqDto signupDto = UserSignupRqDto.builder()
                .email("noah@example.com")
                .password("StrongPassWooord123*")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_USER")
                .build();
        User user = User.builder()
                .email("noah@example.com")
                .password("StrongPassWooord123*")
                .firstName("Noah")
                .lastName("Sebastian")
                .role("ROLE_USER")
                .build();
        UserSignupRsDto responseDto = UserSignupRsDto.builder()
                .email("noah@example.com")
                .firstName("Noah")
                .lastName("Sebastian")
                .build();

        when(objectMapper.map(signupDto, User.class)).thenReturn(user);
        when(userUseCase.registerUser(user)).thenReturn(Mono.just(user));
        when(objectMapper.map(user, UserSignupRsDto.class)).thenReturn(responseDto);

        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(signupDto));

        handler.listenPOSTUserSignup(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(objectValidator).validate(signupDto);
        verify(userUseCase).registerUser(user);
    }

    @Test
    void listenPOSTUserSigninShouldReturnOkWhenValidRequest() {
        UserSigninRqDto signinDto = UserSigninRqDto.builder()
                .email("noah@example.com")
                .password("StrongPassWooord123*")
                .build();
        User user = User.builder()
                .email("noah@example.com")
                .password("StrongPassWooord123*")
                .build();
        UserSigninRsDto responseDto = UserSigninRsDto.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .type("Bearer")
                .expiresIn(3600)
                .build();
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .type("Bearer")
                .expiresIn(3600)
                .build();

        when(objectMapper.map(signinDto, User.class)).thenReturn(user);
        when(userUseCase.authenticateUser(user)).thenReturn(Mono.just(authenticatedUser));
        when(objectMapper.map(authenticatedUser, UserSigninRsDto.class)).thenReturn(responseDto);

        ServerRequest request = MockServerRequest.builder()
                .body(Mono.just(signinDto));

        handler.listenPOSTUserSignin(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(objectValidator).validate(signinDto);
        verify(userUseCase).authenticateUser(user);
    }
}
