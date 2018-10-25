package com.example.webflux.handler;

import com.example.webflux.model.Product;
import com.example.webflux.model.ResponseBody;
import com.example.webflux.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ServerResponse> get(ServerRequest request) {
        Integer productId = Integer.parseInt(request.pathVariable("productId"));

        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(productRepository.findById(productId)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found")))
                                .map(product -> ResponseBody.ok(request.path(), product))
                        , ResponseBody.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(productRepository.findAll()
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found")))
                        .map(product -> ResponseBody.ok(request.path(), product))
                        , ResponseBody.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(request.bodyToMono(Product.class)
                        .flatMap(requestProduct -> productRepository.findById(requestProduct.getId())
                                .flatMap(product -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Product Already Exist")))
                                .switchIfEmpty(productRepository.save(requestProduct))
                        )
                        .map(product -> ResponseBody.created(request.path(), product))
                        , ResponseBody.class);
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(request.bodyToMono(Product.class)
                                .flatMap(requestProduct -> productRepository.findById(requestProduct.getId())
                                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Product Already Exist")))
                                        .flatMap(product -> productRepository.save(requestProduct))
                                )
                                .map(product -> ResponseBody.ok(request.path(), product))
                        , ResponseBody.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Integer productId = Integer.parseInt(request.pathVariable("productId"));

        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(productRepository.findById(productId)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found")))
                                .flatMap(product -> productRepository.delete(product))
                                .then(Mono.just(ResponseBody.ok(request.path(), productId))), ResponseBody.class);
    }


    public Mono<ServerResponse> proxyGet(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .get()
                .uri("/product/{productId}", request.pathVariable("productId"))
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<ResponseBody<Product>>() {})
                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())), ResponseBody.class);
    }

    public Mono<ServerResponse> proxyGetAll(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .get()
                .uri("/product")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<ResponseBody<Product>>() {})
                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())), ResponseBody.class);
    }

    public Mono<ServerResponse> proxyPost(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Product.class)
                        .flatMap(requestBody -> webClientBuilder
                                .build()
                                .post()
                                .uri("/product")
                                .syncBody(requestBody)
                                .retrieve()
                                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                                .bodyToMono(new ParameterizedTypeReference<ResponseBody<Product>>() {})
                                .map(userResponseBody -> ResponseBody.created(request.path(), userResponseBody.getData())))
                , ResponseBody.class);
    }

    public Mono<ServerResponse> proxyPut(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Product.class)
                        .flatMap(requestBody -> webClientBuilder
                                .build()
                                .put()
                                .uri("/user")
                                .syncBody(requestBody)
                                .retrieve()
                                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                                .bodyToMono(new ParameterizedTypeReference<ResponseBody<Product>>() {})
                                .map(userResponseBody -> ResponseBody.ok(request.path(), userResponseBody.getData())))
                , ResponseBody.class);
    }

    public Mono<ServerResponse> proxyDelete(ServerRequest request) {
        return ServerResponse.ok().body(webClientBuilder
                .build()
                .delete()
                .uri("/product/{productId}", request.pathVariable("productId"))
                .retrieve()
                .onStatus(HttpStatus::isError, error -> Mono.error(new ResponseStatusException(error.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<ResponseBody<Integer>>() {})
                .map(responseBody -> ResponseBody.ok(request.path(), responseBody.getData())), ResponseBody.class);
    }
}
