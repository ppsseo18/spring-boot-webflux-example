package com.example.webflux.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public abstract class ResponseBody<T> {

    HttpStatus status;

}
