/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.system.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    public static ErrorMessage errorMessage(HttpStatus httpStatus, String message) {
        ErrorMessage errorMessage = new ErrorMessage(httpStatus, message);
        return errorMessage;
    }

    @Deprecated
    public static ErrorMessage errorMessage(Throwable throwable, HttpServletRequest request) {
        return errorMessage(throwable);
    }

    public static ErrorMessage errorMessage(Throwable throwable) {
        Objects.requireNonNull(throwable);
        if (log.isDebugEnabled()) {
            Throwable temp = throwable;
            while (temp != null) {
                log.debug("throwable class: {}", temp.getClass().getCanonicalName());
                temp = temp.getCause();
            }
        }
        ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(throwable.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String reason = httpStatus.getReasonPhrase();
        if (status != null) {
            httpStatus = status.code();
            reason = status.reason();
            if (StringUtils.isEmpty(reason)) {
                reason = httpStatus.getReasonPhrase();
            }
        }

        ErrorMessage message = new ErrorMessage();
        message.setError(reason);
        message.setMessage(throwable.getMessage());
        return message;
    }

    @ExceptionHandler(Throwable.class)
    public ErrorMessage handleException(Throwable throwable, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        log.warn("Unknown exception", throwable);
        ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(throwable.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            httpStatus = status.code();
        }
        response.setStatus(httpStatus.value());
        return new ErrorMessage(throwable);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorMessage handleAuthenticationException(AuthenticationException exception,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        return new ErrorMessage(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorMessage handleAccessDeniedException(AccessDeniedException exception,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        return new ErrorMessage(HttpStatus.FORBIDDEN, exception.getMessage());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PersistenceException.class)
    public ErrorMessage handlePersistenceException(Throwable throwable,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response)
            throws Exception {
        Throwable exception = throwable.getCause();
        if (exception instanceof SQLException) {
            SQLException sqlException = (SQLException) exception;
            log.warn("error code: {}", sqlException.getErrorCode());
            log.warn("sql exception:\n{}", sqlException.getMessage());
        }
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = buildMessages(exception.getBindingResult());
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, message);
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        log.warn("exception:", ex);
        if (body == null) {
            ErrorMessage errorMessage = new ErrorMessage(status);
            body = errorMessage;
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            ConstraintViolationException.class
    })
    public ErrorMessage handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        Set<String> set = new LinkedHashSet<>();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            if (path instanceof PathImpl) {
                PathImpl path1 = (PathImpl) path;
                String name = path1.getLeafNode().getName();
                set.add(String.format("Property [%s]: %s.", name, violation.getMessage()));
            } else {
                set.add(violation.getMessage());
            }
        }
        String result = String.join(" ", set);
        return new ErrorMessage(HttpStatus.BAD_REQUEST, result);
    }

    private String buildMessages(BindingResult result) {
        StringBuilder resultBuilder = new StringBuilder();

        List<ObjectError> errors = result.getAllErrors();
        if (errors != null && errors.size() > 0) {
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    String fieldName = fieldError.getField();
                    String fieldErrMsg = StringUtils.trim(fieldError.getDefaultMessage());
                    log.warn("Field[{}]: {}", fieldName, fieldErrMsg);
                    resultBuilder.append(fieldErrMsg);
                    if (!StringUtils.endsWith(fieldErrMsg, ".")) {
                        resultBuilder.append(".");
                    }
                }
            }
        }
        return resultBuilder.toString();
    }


    @Data
    @NoArgsConstructor
    public static class ErrorMessage {
        @Setter(value = AccessLevel.NONE)
        private final Date date = new Date();
        private String error;
        private String message;

        public ErrorMessage(HttpStatus httpStatus, String message) {
            this.error = httpStatus.getReasonPhrase();
            this.message = message;
        }

        public ErrorMessage(HttpStatus httpStatus) {
            this.error = httpStatus.getReasonPhrase();
            this.message = httpStatus.getReasonPhrase();
        }

        public ErrorMessage(Throwable throwable) {
            Objects.requireNonNull(throwable);
            if (log.isDebugEnabled()) {
                Throwable temp = throwable;
                while (temp != null) {
                    log.debug("throwable class: {}", temp.getClass().getCanonicalName());
                    temp = temp.getCause();
                }
            }
            ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(
                    throwable.getClass(), ResponseStatus.class);
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            String reason = httpStatus.getReasonPhrase();
            if (responseStatus != null) {
                httpStatus = responseStatus.code();
                reason = responseStatus.reason();
                if (StringUtils.isEmpty(reason)) {
                    reason = httpStatus.getReasonPhrase();
                }
            }
            this.setError(reason);
            this.setMessage(throwable.getMessage());
        }
    }

}
