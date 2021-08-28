package com.ngoctm.app.ws.exceptions;

import java.io.Serializable;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = -8510194424018563044L;

    public UserServiceException(String message) {
        super(message);
    }
}

