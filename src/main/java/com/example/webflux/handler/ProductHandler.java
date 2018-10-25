package com.example.webflux.handler;

import com.example.webflux.model.Product;
import com.example.webflux.model.ResponseBody;
import com.example.webflux.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

    @Autowired
    private ProductRepository productRepository;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(productRepository.findAll()
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found")))
                        .map(product -> ResponseBody.ok(request.path(), product))
                        , ResponseBody.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
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

}
