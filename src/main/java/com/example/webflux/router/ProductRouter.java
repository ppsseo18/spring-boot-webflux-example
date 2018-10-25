package com.example.webflux.router;

import com.example.webflux.filter.BasicAuthenticationFilter;
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
    private BasicAuthenticationFilter basicAuthenticationFilter;

    @Autowired
    private ProductHandler productHandler;

    @Bean
    public RouterFunction<ServerResponse> productRoutes() {
        return RouterFunctions.route(RequestPredicates.GET("/product/{productId}"), productHandler::get)
                .andRoute(RequestPredicates.GET("/product").and(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM))), productHandler::getAll)
                .andRoute(RequestPredicates.POST("/product"), productHandler::post)
                .andRoute(RequestPredicates.PUT("/product").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), productHandler::put)
                .andRoute(RequestPredicates.DELETE("/product/{productId}"), productHandler::delete)
                .filter(basicAuthenticationFilter::filter)
                .andRoute(RequestPredicates.GET("/proxy/product/{productId}"), productHandler::proxyGet)
                .andRoute(RequestPredicates.GET("/proxy/product").and(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM))), productHandler::proxyGetAll)
                .andRoute(RequestPredicates.POST("/proxy/product"), productHandler::proxyPost)
                .andRoute(RequestPredicates.PUT("/proxy/product").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), productHandler::proxyPut)
                .andRoute(RequestPredicates.DELETE("/proxy/product/{productId}"), productHandler::proxyDelete);
    }
}
