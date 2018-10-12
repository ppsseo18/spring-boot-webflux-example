package com.example.webflux.router;

import com.example.webflux.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Autowired
    private UserHandler userHandler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET("/people/{userId}"), userHandler::get)
                .andRoute(RequestPredicates.POST("/people").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::post)
                .andRoute(RequestPredicates.PUT("/people").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::put)
                .andRoute(RequestPredicates.DELETE("/people/{userId}"), userHandler::delete);
    }
}
