package com.example.webflux.service;

import com.example.webflux.ResponseBody.ResponseBody;
import com.example.webflux.entity.User;
import com.example.webflux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Scheduler scheduler;

    public Mono<ResponseBody<User>> read(String requestUserId, String path) {
        return this.findById(requestUserId)
                .flatMap(user -> user.isPresent() ?
                        Mono.just(user.get()) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> new ResponseBody<>(new Date(), path, HttpStatus.OK.value(), user))
                .publishOn(Schedulers.parallel());
    }

    public Mono<ResponseBody<User>> create(User requestUser, String path) {
        return this.findById(requestUser.getUserId())
                .flatMap(user -> user.isPresent() ?
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist")) :
                        this.save(requestUser))
                .map(user -> new ResponseBody<>(new Date(), path, HttpStatus.CREATED.value(), user))
                .publishOn(Schedulers.parallel());
    }

    public Mono<ResponseBody<User>> update(User requestUser, String path) {
        return this.findById(requestUser.getUserId())
                .flatMap(user -> user.isPresent() ?
                        this.save(requestUser) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> new ResponseBody<>(new Date(), path, HttpStatus.OK.value(), user))
                .publishOn(Schedulers.parallel());
    }

    public Mono<ResponseBody<String>> delete(String requestUserId, String path) {
        return this.findById(requestUserId)
                .flatMap(user -> user.isPresent() ?
                        this.deleteById(requestUserId) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found")))
                .map(user -> new ResponseBody<>(new Date(), path, HttpStatus.OK.value(), user))
                .publishOn(Schedulers.parallel());
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

    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).subscribeOn(scheduler).log();
    }

}
