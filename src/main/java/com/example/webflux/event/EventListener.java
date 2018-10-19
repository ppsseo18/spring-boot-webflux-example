package com.example.webflux.event;

public interface EventListener<T> {
    void onEvent(String eventName, T data);

    void onComplete();
}
