package co.com.assessment.api;

import co.com.assessment.api.dto.request.UserSigninRsDto;
import co.com.assessment.api.dto.request.UserSignupRqDto;
import co.com.assessment.api.dto.response.UserSignupRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.User;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.usecase.UserSignupUseCase;
import org.reactivecommons.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserSignupUseCase userSignupUseCase;
    private final ObjectValidator objectValidator;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> listenPOSTUserSignup(ServerRequest serverRequest) {
       // return ServerResponse.ok().bodyValue("");

        return serverRequest.bodyToMono(UserSignupRqDto.class)
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.EMAIL_ALREADY_REGISTERED)))
                .doOnNext(objectValidator::validate)
                .map(dto -> objectMapper.map(dto, User.class))
                .flatMap(model-> userSignupUseCase.registerUser(Mono.just(model)))
                .map(user -> objectMapper.map(user, UserSignupRsDto.class))
                .flatMap(this::buildResponse);
    /*    return serverRequest.bodyToMono(UserSignupRqDto.class)
                .switchIfEmpty(Mono.error(()-> new RuntimeException("Invalid request")))
                .flatMap(requestDto -> userSignupUseCase.registerUser())
                .flatMap(requestDto -> buildResponse(serverRequest, requestDto));*/
    }

    public Mono<ServerResponse> listenPOSTUserSignin(ServerRequest serverRequest) {
        // useCase2.logic();
        return serverRequest.bodyToMono(UserSigninRsDto.class)
                .doOnNext(objectValidator::validate)
                .map(dto -> objectMapper.map(dto, User.class))
                .flatMap(model -> userSignupUseCase.login(Mono.just(model)))
                .flatMap(this::buildResponse);

    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    private Mono<ServerResponse> buildResponse(Object userSignupRqDto){
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
