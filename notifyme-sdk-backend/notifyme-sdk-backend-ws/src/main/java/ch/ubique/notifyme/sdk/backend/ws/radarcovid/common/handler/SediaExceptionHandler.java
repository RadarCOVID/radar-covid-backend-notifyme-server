/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.common.handler;

import java.util.stream.Collectors;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.exception.RadarCovidServerException;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.ErrorDto;

@RestControllerAdvice(basePackages = "ch.ubique.notifyme.sdk.backend.ws.radarcovid")
public class SediaExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(SediaExceptionHandler.class);

	/**
     * This method handles RadarCovid Exceptions.
     *
     * @param exception the thrown exception
     * @param request the WebRequest
     * @return returns a response with error message
     */
    @ExceptionHandler(RadarCovidServerException.class)
    public ResponseEntity<?> handleConfigurationServerExceptions(RadarCovidServerException exception, WebRequest request) {
    	logger.warn("RadarCovid server response preventation due to: {}", exception.getMessage());
        return handleError(exception.getMessage(), exception, request, HttpStatus.BAD_REQUEST);
    }
	
    /**
     * This method handles Validation Exceptions.
     *
     * @return ResponseEntity<Object> returns Bad Request
     */   
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errors = String.join(", ", exception.getBindingResult().getAllErrors().stream()
				.map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
		logger.error("Invalid request: {}", errors);
		return handleError("Invalid request: " + errors, exception, request, HttpStatus.BAD_REQUEST);
	}
	
    /**
     * This method handles unknown Exceptions and Server Errors.
     *
     * @param exception the thrown exception
     * @param request the WebRequest
	 * @return returns a response with error message
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<?> handleInternal(Exception exception, WebRequest request) {
		logger.error("Unable to handle {}", request.getDescription(false), exception);
		return handleError("Unable to handle " + request.getDescription(false), exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	    
    @ExceptionHandler({
        AccessDeniedException.class,
        AuthenticationException.class,
        InsufficientAuthenticationException.class
	})
	public ResponseEntity<?> handleAccessDeniedException(Exception exception, WebRequest request) {
    	logger.error("Access denied: {}", request.getDescription(false));
	    return handleError("Access denied to " + request.getDescription(false), exception, request, HttpStatus.FORBIDDEN);
	}
	
    /**
     * This method create an ErrorDto object
     * @param errorCode code of error
     * @param errorDescription error description
     * @param request origin request
     * @param status status code
     * @return returns a ErrorDto object 
     */
	private ErrorDto createError(String errorDescription, WebRequest request, HttpStatus status) {
		return new ErrorDto(Long.valueOf(System.currentTimeMillis()), 
				status.value(), errorDescription, ((ServletWebRequest) request).getRequest().getRequestURI());
	}
	
	/**
	 * This method handle error
	 * @param errorDescription description of error
	 * @param exception the thrown exception
	 * @param request the WebRequest
	 * @param status status code
	 * @return returns a response with error message
	 */
	private ResponseEntity<Object> handleError(String errorDescription, Exception exception, WebRequest request, HttpStatus status) {
		return handleExceptionInternal(exception, createError(errorDescription, request, status), new HttpHeaders(), status, request);
	}
	
}
