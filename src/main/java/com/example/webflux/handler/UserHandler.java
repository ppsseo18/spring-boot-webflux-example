package com.example.webflux.handler;

import com.example.webflux.ResponseBody.ResponseBody;
import com.example.webflux.entity.User;
import com.example.webflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    public Mono<ServerResponse> get(ServerRequest request) {
        String path = request.path();
        String userId = request.pathVariable("userId");
        Mono<ResponseBody> body = userService
                .read(userId)
                .map(user -> ResponseBodyHandler.handleResponseBody(HttpStatus.OK, path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body, ResponseBody.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        String path = request.path();
        MediaType mediaType = request.headers().accept().get(0);
        Flux<ResponseBody> body = userService
                .readAll()
                .map(user -> ResponseBodyHandler.handleResponseBody(HttpStatus.OK, path, user));

        return ServerResponse
                .ok()
                .contentType(mediaType)
                .body(body, ResponseBody.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        String path = request.path();
        Mono<ResponseBody> body = request
                .bodyToMono(User.class).log()
                .flatMap(user -> userService.create(user))
                .map(user -> ResponseBodyHandler.handleResponseBody(HttpStatus.CREATED, path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body, ResponseBody.class);
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        String path = request.path();
        Mono<ResponseBody> body = request
                .bodyToMono(User.class)
                .flatMap(user -> userService.update(user))
                .map(user -> ResponseBodyHandler.handleResponseBody(HttpStatus.OK, path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body, ResponseBody.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String path = request.path();
        String userId = request.pathVariable("userId");
        Mono<ResponseBody> body = userService
                .delete(userId)
                .map(user -> ResponseBodyHandler.handleResponseBody(HttpStatus.OK, path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body, ResponseBody.class);
    }
}
