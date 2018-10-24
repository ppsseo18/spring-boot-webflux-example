package com.example.webflux.handler;

import com.example.webflux.model.ResponseBody;
import com.example.webflux.model.User;
import com.example.webflux.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

@Component
public class UserHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ServerResponse> get(ServerRequest request) {
        String path = request.path();
        String userId = request.pathVariable("userId");
        Mono<ResponseBody> responseBodyMono = async(() -> userService.read(userId))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> ResponseBody.ok(path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBodyMono, ResponseBody.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        String path = request.path();
        MediaType mediaType = request.headers().accept().get(0);
        Flux<ResponseBody> responseBodyMono = async(() -> userService.readAll())
                .publishOn(Schedulers.parallel())
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> ResponseBody.ok(path, user));

        return ServerResponse
                .ok()
                .contentType(mediaType)
                .body(responseBodyMono, ResponseBody.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        String path = request.path();
        Mono<ResponseBody> responseBodyMono = request
                .bodyToMono(User.class)
                .flatMap(user -> async(() ->userService.create(user)))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist")))
                .map(user -> ResponseBody.created(path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBodyMono, ResponseBody.class);
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        String path = request.path();
        Mono<ResponseBody> responseBodyMono = request
                .bodyToMono(User.class)
                .flatMap(user -> async(() -> userService.update(user)))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> ResponseBody.ok(path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBodyMono, ResponseBody.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String path = request.path();
        String userId = request.pathVariable("userId");
        Mono<ResponseBody> responseBodyMono = async(() ->userService.delete(userId))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> ResponseBody.ok(path, user));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBodyMono, ResponseBody.class);
    }

    public Mono<ServerResponse> proxyGet(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .get()
                .uri("/user/{userId}", request.pathVariable("userId"))
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<ResponseBody<User>>() {})
                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())), ResponseBody.class);
    }

    public Mono<ServerResponse> proxyGetAll(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .get()
                .uri("/user")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<ResponseBody<User>>() {})
                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())), ResponseBody.class);
    }

    public Mono<ServerResponse> proxyPost(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(User.class)
                        .flatMap(requestBody -> webClientBuilder
                            .build()
                            .post()
                            .uri("/user")
                            .syncBody(requestBody)
                            .retrieve()
                            .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                            .bodyToMono(new ParameterizedTypeReference<ResponseBody<User>>() {})
                            .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())))
                , ResponseBody.class);
    }

    public Mono<ServerResponse> proxyPut(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(User.class)
                        .flatMap(requestBody -> webClientBuilder
                                .build()
                                .put()
                                .uri("/user")
                                .syncBody(requestBody)
                                .retrieve()
                                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                                .bodyToMono(new ParameterizedTypeReference<ResponseBody<User>>() {})
                                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())))
                , ResponseBody.class);
    }

    public Mono<ServerResponse> proxyDelete(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .delete()
                .uri("/user/{userId}", request.pathVariable("userId"))
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<ResponseBody<String>>() {})
                .map(responseBody -> ResponseBody.ok(request.path(), responseBody.getData())), ResponseBody.class);
    }

    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).subscribeOn(scheduler);
    }
}
