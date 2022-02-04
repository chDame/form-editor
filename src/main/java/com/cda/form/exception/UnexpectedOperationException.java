package com.cda.form.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author christophe.dame
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnexpectedOperationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7593616210087047797L;

    public UnexpectedOperationException() {
        super();
    }

    public UnexpectedOperationException(Exception e) {
        super(e);
    }

    public UnexpectedOperationException(String message) {
        super(message);
    }

    public UnexpectedOperationException(String message, Exception e) {
        super(message, e);
    }
}