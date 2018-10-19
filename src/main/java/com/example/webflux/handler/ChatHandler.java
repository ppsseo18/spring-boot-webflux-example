package com.example.webflux.handler;

import com.example.webflux.event.EventProcessor;
import com.example.webflux.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;

@Component
public class ChatHandler implements WebSocketHandler {

    private static EventProcessor<String> globalMessageStream = new EventProcessor<String>() {
        private ArrayList<EventListener<String>> listenerList = new ArrayList<>();

        @Override
        public void onNext(String message) {
            this.emit(message);
        }

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void onComplete(EventListener listener) {
            this.listenerList.remove(listener);
            this.emit("One user left the session!");
        }

        @Override
        public void register(EventListener listener) {
            this.emit("New user entered the session!");
            this.listenerList.add(listener);
        }

        @Override
        public void emit(String item) {
            this.listenerList.forEach(listener -> listener.onEvent("data", item));
        }

        @Override
        public void complete() {
            this.listenerList.forEach(listener -> listener.onComplete());
        }

    };

    private static class GlobalMessageListener<T> implements EventListener<T> {
        private FluxSink<T> subscriber;

        public void setSubscriber(FluxSink<T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onEvent(String eventName, T data) {
            subscriber.next(data);
        }

        @Override
        public void onComplete() {
            subscriber.complete();
        }
    }

    @Override

    public Mono<Void> handle(WebSocketSession webSocketSession) {
        Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(2));

        GlobalMessageListener<String> messageListener = new GlobalMessageListener<>();
        globalMessageStream.register(messageListener);

        webSocketSession
                .receive()
                .doOnNext(WebSocketMessage::retain)
                .map(WebSocketMessage::getPayloadAsText)
                .subscribe(globalMessageStream::onNext, globalMessageStream::onError, () -> globalMessageStream.onComplete(messageListener));

        Flux<String> chat = Flux.create(subscriber -> messageListener.setSubscriber(subscriber));


        return webSocketSession
                .send(Flux.merge(
                        chat.map(text -> webSocketSession.textMessage(text)) ,
                        intervalFlux.map(value -> webSocketSession.textMessage(value.toString()))
                        )
                );
    }
}
