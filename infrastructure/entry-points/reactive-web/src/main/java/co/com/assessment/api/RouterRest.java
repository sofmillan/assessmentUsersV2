package co.com.assessment.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/user/signup"), handler::listenPOSTUserSignup)
                .andRoute(POST("/user/signin"), handler::listenPOSTUserSignin)
                .andRoute(RequestPredicates.all(), request ->{
                    throw new MethodNotAllowedException(request.method(), List.of());
                });
    }
}
