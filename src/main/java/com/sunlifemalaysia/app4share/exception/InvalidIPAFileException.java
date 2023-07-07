package com.sunlifemalaysia.app4share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidIPAFileException extends ResponseStatusException {

    public InvalidIPAFileException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

}
