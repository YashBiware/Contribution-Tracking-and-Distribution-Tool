package com.ctdt.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RecalculationLockException extends RuntimeException{
    public RecalculationLockException(Long pid){super("Recalculation in progress for project "+pid+". Try again shortly.");}
}
