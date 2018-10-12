package com.example.webflux.service;

import com.example.webflux.ResponseBody.ErrorBody;
import com.example.webflux.ResponseBody.ResponseBody;
import com.example.webflux.ResponseBody.SuccessBody;
import com.example.webflux.entity.User;
import com.example.webflux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.Callable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Scheduler scheduler;

    public Mono<ResponseBody<User>> read(String requestUserId) {
        return this.findById(requestUserId)
                .flatMap(user -> user.isPresent() ?
                        Mono.just(user.get()) :
                        Mono.error(new Exception("User Not Found")))
                .map(user -> (ResponseBody<User>)new SuccessBody<User>(HttpStatus.OK, user))
                .publishOn(Schedulers.single())
                .onErrorResume(error -> error.getMessage().equals("User Not Found") ?
                        Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.NOT_FOUND, error.getMessage())) :
                        Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage())));
    }

    public Mono<ResponseBody<User>> create(User requestUser) {
        return this.findById(requestUser.getUserId())
                .flatMap(user -> user.isPresent() ?
                        Mono.error(new Exception("User Already Exist")) :
                        this.save(requestUser))
                .map(user -> (ResponseBody<User>)new SuccessBody<User>(HttpStatus.CREATED, user))
                .publishOn(Schedulers.single())
                .onErrorResume(error -> error.getMessage().equals("User Already Exist") ?
                        Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.CONFLICT, error.getMessage())) :
                        Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage())));
    }

    public Mono<ResponseBody<User>> update(User requestUser) {
        return this.findById(requestUser.getUserId())
                .flatMap(user -> user.isPresent() ?
                        this.save(requestUser) :
                        Mono.error(new Exception("User Not Found")))
                .map(user -> (ResponseBody<User>)new SuccessBody<User>(HttpStatus.OK, user))
                .publishOn(Schedulers.single())
                .onErrorResume(error -> error.getMessage().equals("User Not Found") ?
                                Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.NOT_FOUND, error.getMessage())) :
                                Mono.just((ResponseBody<User>)new ErrorBody<User>(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage())));
    }

    public Mono<ResponseBody<String>> delete(String requestUserId) {
        return this.findById(requestUserId)
                .flatMap(user -> user.isPresent() ?
                        this.deleteById(requestUserId) :
                        Mono.error(new Exception("User Not Found")))
                .map(message -> (ResponseBody<String>)new SuccessBody<String>(HttpStatus.OK, message))
                .publishOn(Schedulers.single())
                .onErrorResume(error -> error.getMessage().equals("User Not Found") ?
                        Mono.just((ResponseBody<String>)new ErrorBody<String>(HttpStatus.NOT_FOUND, error.getMessage())) :
                        Mono.just((ResponseBody<String>)new ErrorBody<String>(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage())));
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
