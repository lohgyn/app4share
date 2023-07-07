package com.sunlifemalaysia.app4share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidApkFileException extends ResponseStatusException {

    public InvalidApkFileException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

}
