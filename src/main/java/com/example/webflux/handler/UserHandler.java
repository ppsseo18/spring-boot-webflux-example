package com.example.webflux.handler;

import com.example.webflux.entity.User;
import com.example.webflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    public Mono<ServerResponse> get(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return userService
                .read(userId)
                .flatMap(userResponseBody -> ServerResponse.status(userResponseBody.getStatus()).contentType(MediaType.APPLICATION_JSON).syncBody(userResponseBody)).log();
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        Mono<User> body = request.bodyToMono(User.class).log();
        return body
                .flatMap(user -> userService.create(user))
                .flatMap(userResponseBody -> ServerResponse.status(userResponseBody.getStatus()).contentType(MediaType.APPLICATION_JSON).syncBody(userResponseBody)).log();
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        Mono<User> body = request.bodyToMono(User.class).log();
        return body
                .flatMap(user -> userService.update(user))
                .flatMap(userResponseBody -> ServerResponse.status(userResponseBody.getStatus()).contentType(MediaType.APPLICATION_JSON).syncBody(userResponseBody)).log();
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return userService
                .delete(userId)
                .flatMap(userResponseBody -> ServerResponse.status(userResponseBody.getStatus()).contentType(MediaType.APPLICATION_JSON).syncBody(userResponseBody)).log();
    }
}
