package com.cda.form.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author christophe.dame
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7593616210087047797L;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(Exception e) {
        super(e);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}