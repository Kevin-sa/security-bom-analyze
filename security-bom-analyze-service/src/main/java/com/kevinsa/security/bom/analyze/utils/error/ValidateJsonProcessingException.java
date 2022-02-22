package com.kevinsa.security.bom.analyze.utils.error;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UncheckedIOException;

public class ValidateJsonProcessingException extends UncheckedIOException {

    public ValidateJsonProcessingException(JsonProcessingException cause) {
        super(cause.getMessage(), cause);
    }
}