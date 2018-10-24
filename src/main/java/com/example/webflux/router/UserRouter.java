package com.example.webflux.router;

import com.example.webflux.filter.BasicAuthenticationFilter;
import com.example.webflux.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class UserRouter {

    @Autowired
    private BasicAuthenticationFilter basicAuthenticationFilter;

    @Autowired
    private UserHandler userHandler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET("/user/{userId}"), userHandler::get)
                .andRoute(RequestPredicates.GET("/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM))), userHandler::getAll)
                .andRoute(RequestPredicates.POST("/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::post)
                .andRoute(RequestPredicates.PUT("/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::put)
                .andRoute(RequestPredicates.DELETE("/user/{userId}"), userHandler::delete)
                .filter(basicAuthenticationFilter::filter)
                .andRoute(RequestPredicates.GET("/proxy/user/{userId}"), userHandler::proxyGet)
                .andRoute(RequestPredicates.GET("/proxy/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM))), userHandler::proxyGetAll)
                .andRoute(RequestPredicates.POST("/proxy/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::proxyPost)
                .andRoute(RequestPredicates.PUT("/proxy/user").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)).and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), userHandler::proxyPut)
                .andRoute(RequestPredicates.DELETE("/proxy//user/{userId}"), userHandler::proxyDelete);
    }
}
