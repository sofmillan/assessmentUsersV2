package co.com.assessment.api;

import co.com.assessment.api.dto.request.UserSigninRqDto;
import co.com.assessment.api.dto.request.UserSignupRqDto;
import co.com.assessment.api.dto.response.UserSigninRsDto;
import co.com.assessment.api.dto.response.UserSignupRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.User;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.usecase.UserUseCase;
import org.reactivecommons.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final ObjectValidator objectValidator;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> listenPOSTUserSignup(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserSignupRqDto.class)
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.INVALID_REQUEST)))
                .doOnNext(objectValidator::validate)
                .map(dto -> objectMapper.map(dto, User.class))
                .flatMap(model-> userUseCase.registerUser(Mono.just(model)))
                .map(user -> objectMapper.map(user, UserSignupRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenPOSTUserSignin(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserSigninRqDto.class)
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.INVALID_REQUEST)))
                .doOnNext(objectValidator::validate)
                .map(dto -> objectMapper.map(dto, User.class))
                .flatMap(model -> userUseCase.login(Mono.just(model)))
                .map(model -> objectMapper.map(model, UserSigninRsDto.class))
                .flatMap(this::buildResponse);

    }
    private Mono<ServerResponse> buildResponse(Object userSignupRqDto){
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
