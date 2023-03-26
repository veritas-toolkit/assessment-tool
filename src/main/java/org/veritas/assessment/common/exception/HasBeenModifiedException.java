package org.veritas.assessment.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The data has been modified.")
public class HasBeenModifiedException extends AbstractVeritasException {

    public HasBeenModifiedException(String message) {
        super(message);
    }
}
