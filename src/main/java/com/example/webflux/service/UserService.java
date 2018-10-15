package com.example.webflux.service;

import com.example.webflux.entity.User;
import com.example.webflux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Scheduler scheduler;

    public Mono<User> read(String requestUserId) {
        return this.findById(requestUserId)
                .publishOn(Schedulers.parallel())
                .flatMap(user -> user.isPresent() ?
                        Mono.just(user.get()) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"))
                );
    }

    public Flux<User> readAll() {
        return this.findAll()
                .publishOn(Schedulers.parallel())
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No User")));

    }

    public Mono<User> create(User requestUser) {
        return this.findById(requestUser.getUserId())
                .publishOn(Schedulers.parallel())
                .flatMap(user -> user.isPresent() ?
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist")) :
                        this.save(requestUser).publishOn(Schedulers.parallel())
                );

    }

    public Mono<User> update(User requestUser) {
        return this.findById(requestUser.getUserId())
                .publishOn(Schedulers.parallel())
                .flatMap(user -> user.isPresent() ?
                        this.save(requestUser).publishOn(Schedulers.parallel()) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"))
                );
    }

    public Mono<String> delete(String requestUserId) {
        return this.findById(requestUserId)
                .publishOn(Schedulers.parallel())
                .flatMap(user -> user.isPresent() ?
                        this.deleteById(requestUserId).publishOn(Schedulers.parallel()) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"))
                );
    }

    private Mono<Optional<User>> findById(String userId) {
        return async(() -> userRepository.findById(userId));
    }

    private Mono<User> save(User user){
        return async(() -> userRepository.save(user));
    }

    private Mono<String> deleteById(String userId) {
        return async(() -> {
            userRepository.deleteById(userId);
            return userId;
        });
    }

    private Mono<List<User>> findAll() {
        return async(() -> userRepository.findAll());
    }

    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).subscribeOn(scheduler);
    }

}
