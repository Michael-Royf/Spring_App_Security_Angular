package com.michael.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.michael.document.exceptions.payload.ApiException;
import com.michael.document.exceptions.payload.ExistException;
import com.michael.document.exceptions.payload.NotFoundException;
import com.michael.document.payload.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ResponseUtils {

    //нормальный ответ
    public static Response getResponse(HttpServletRequest request,
                                       Map<?, ?> data,
                                       String message,
                                       HttpStatus status) {
        return Response.builder()
                .time(LocalDateTime.now().toString())
                .code(status.value())
                .path(request.getRequestURI())
                .status(HttpStatus.valueOf(status.value()))
                .message(message)
                .exception(EMPTY)
                .data(data)
                .build();
    }


    private static final BiFunction<Exception, HttpStatus, String> errorReason =
            (exception, httpStatus) -> {
                if (httpStatus.isSameCodeAs(FORBIDDEN)) {
                    return "You do not have enough permission";
                }
                if (httpStatus.isSameCodeAs(UNAUTHORIZED)) {
                    return "You are not logged in";
                }
                if (exception instanceof DisabledException ||
                        exception instanceof LockedException ||
                        exception instanceof BadCredentialsException ||
                        exception instanceof CredentialsExpiredException ||
                        exception instanceof ApiException ||
                        exception instanceof ExistException ||
                        exception instanceof NotFoundException
                ) {
                    return exception.getMessage();
                }
                if (httpStatus.is5xxServerError()) {
                    return "An internal server error occurred";
                } else {
                    return "An error occurred. Please try again";
                }
            };


    private static final BiConsumer<HttpServletResponse, Response> writeResponse =
            ((httpServletResponse, response) -> {
                try {
                    var outputStream = httpServletResponse.getOutputStream();
                    new ObjectMapper().writeValue(outputStream, response);
                    outputStream.flush();
                } catch (Exception exception) {
                    throw new ApiException(exception.getMessage());
                }
            });


    public static void handleErrorResponse(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Exception exception) {
        if (exception instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, exception, FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        } else if (exception instanceof InsufficientAuthenticationException) {
            Response apiResponse = getErrorResponse(request, response, exception, UNAUTHORIZED);
            writeResponse.accept(response, apiResponse);
        } else if (exception instanceof MismatchedInputException) {
            Response apiResponse = getErrorResponse(request, response, exception, BAD_REQUEST);
            writeResponse.accept(response, apiResponse);
        } else if (exception instanceof DisabledException || exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof CredentialsExpiredException || exception instanceof ApiException) {
            Response apiResponse = getErrorResponse(request, response, exception, BAD_REQUEST);
            writeResponse.accept(response, apiResponse);
        } else {
            Response apiResponse = getErrorResponse(request, response, exception, INTERNAL_SERVER_ERROR);
            writeResponse.accept(response, apiResponse);
        }
    }

    public static Response handleErrorResponse(String message,
                                               String exception,
                                               HttpServletRequest request,
                                               HttpStatusCode status) {
        return Response.builder()
                .time(LocalDateTime.now().toString())
                .code(status.value())
                .path(request.getRequestURI())
                .status(HttpStatus.valueOf(status.value()))
                .message(message)
                .exception(exception)
                .data(emptyMap())
                .build();
    }


    private static Response getErrorResponse(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Exception exception,
                                             HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        return Response.builder()
                .time(LocalDateTime.now().toString())
                .code(status.value())
                .path(request.getRequestURI())
                .status(HttpStatus.valueOf(status.value()))
                .message(errorReason.apply(exception, status))
                .exception(getRootCauseMessage(exception))
                .data(emptyMap())
                .build();
    }
}
