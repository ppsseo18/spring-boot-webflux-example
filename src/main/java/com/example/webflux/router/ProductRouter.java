package com.example.webflux.router;

import com.example.webflux.handler.ProductHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Autowired
    private ProductHandler productHandler;

    @Bean
    public RouterFunction<ServerResponse> productRoutes() {
        return RouterFunctions.route(RequestPredicates.POST("/product"), productHandler::create)
                .andRoute(RequestPredicates.GET("/product").and(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM))), productHandler::getAll);
    }
}
