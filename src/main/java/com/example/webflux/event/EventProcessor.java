package com.example.webflux.event;

public interface EventProcessor<T> {
    void onNext(T message);

    void onError(Throwable error);

    void onComplete(EventListener listener);

    void register(EventListener listener);

    void emit(T item);

    void complete();
}
