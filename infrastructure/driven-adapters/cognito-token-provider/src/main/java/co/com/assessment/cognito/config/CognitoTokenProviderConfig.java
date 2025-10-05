package co.com.assessment.cognito.config;


import org.springframework.context.annotation.Configuration;


@Configuration
public class CognitoTokenProviderConfig {
/*    @Bean
    public TokenProvider cognitoTokenProvider(@Value("${adapter.cognito.endpoint}") String endpoint,
                                              @Value("${adapter.cognito.timeout}") int timeout,
                                              WebClient.Builder builder, Mono<CognitoCredentials> provider) {
        WebClient client = getWebClientCognito(builder, endpoint, timeout);
        return new CognitoTokenProvider(client, provider);
    }

    @Bean
    public Mono<CognitoCredentials> cognitoCredentialsProvider(@Value("${adapter.cognito.secret}") String secret,
                                                                GenericManagerAsync manager) throws SecretException {
        return manager.getSecret(secret, CognitoCredentials.class);
    }

    private WebClient getWebClientCognito(WebClient.Builder builder, String endpoint, int timeout) {
        return builder
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .clientConnector(getClientHttpConnector(timeout))
                .build();
    }

    private ClientHttpConnector getClientHttpConnector(int timeout) {
        *//*
        IF YO REQUIRE APPEND SSL CERTIFICATE SELF SIGNED: this should be in the default cacerts trustore
        *//*
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }*/

}
