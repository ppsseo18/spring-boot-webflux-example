package com.example.webflux.service;

import com.example.webflux.entity.User;
import com.example.webflux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Scheduler scheduler;

    public Mono<User> read(String requestUserId) {
        return async(() -> userRepository.findById(requestUserId).orElse(null))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")));
    }

    public Flux<User> readAll() {
        return async(() -> userRepository.findAll())
                .publishOn(Schedulers.parallel())
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No User")));

    }

    @Transactional
    public Mono<User> create(User requestUser) {
        return async(() -> userRepository.findById(requestUser.getUserId()).isPresent() ? null : userRepository.save(requestUser))
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist")));

    }

    @Transactional
    public Mono<User> update(User requestUser) {
        return async(() -> userRepository.findById(requestUser.getUserId()).isPresent() ? userRepository.save(requestUser) : null)
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")));
    }

    @Transactional
    public Mono<String> delete(String requestUserId) {
        return async(() -> {
                    if(userRepository.findById(requestUserId).isPresent()) {
                        userRepository.deleteById(requestUserId);
                        return requestUserId;
                    } else {
                        return null;
                    }
                })
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")));
    }


    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).subscribeOn(scheduler);
    }

}
