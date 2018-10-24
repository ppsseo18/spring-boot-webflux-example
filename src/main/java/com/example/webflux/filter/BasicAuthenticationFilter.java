package com.example.webflux.filter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {
        if(serverRequest.headers().header("Authorization").isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final String authorization = serverRequest.headers().header("Authorization").get(0);
        if (!(authorization != null && authorization.toLowerCase().startsWith("basic"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);

        if(values.length != 2) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if(!(values[0].equals("user") && values[1].equals("password"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return handlerFunction.handle(serverRequest);
    }
}
