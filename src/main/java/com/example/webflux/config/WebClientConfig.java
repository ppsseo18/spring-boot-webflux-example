package com.example.webflux.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder getWebclientBuilder() {
        ReactorClientHttpConnector httpConnector =new ReactorClientHttpConnector(options -> options
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .compression(true)
                .afterNettyContextInit(ctx -> {
                    ctx.addHandlerLast(new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
                    ctx.addHandlerLast(new WriteTimeoutHandler(3000, TimeUnit.MILLISECONDS));
                }));

        return WebClient.builder()
                .clientConnector(httpConnector)
                .baseUrl("http://localhost:8000")
                .defaultHeader(HttpHeaders.USER_AGENT, "webflux example")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunctions.basicAuthentication("user", "password"))
                .filter((clientRequest, next) -> {
                    LoggerFactory.getLogger(WebClient.class).info("Send Request: " + clientRequest.headers().toString());
                    return next.exchange(clientRequest);
                });
    }

}
