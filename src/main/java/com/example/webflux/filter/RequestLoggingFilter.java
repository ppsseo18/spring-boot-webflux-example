package com.example.webflux.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {
        logger.info("Handle request: " + serverRequest.headers().toString());
        return handlerFunction.handle(serverRequest);
    }
}
