package com.example.booking.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiExceptions.NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ApiExceptions.NotFoundException ex, WebRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(ApiExceptions.ConflictException.class)
	public ResponseEntity<ApiError> handleConflict(ApiExceptions.ConflictException ex, WebRequest request) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(ApiExceptions.ForbiddenException.class)
	public ResponseEntity<ApiError> handleForbidden(ApiExceptions.ForbiddenException ex, WebRequest request) {
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
	}

	@ExceptionHandler(ApiExceptions.BadRequestException.class)
	public ResponseEntity<ApiError> handleBadRequest(ApiExceptions.BadRequestException ex, WebRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));
		return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, WebRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
	}

	private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, WebRequest request) {
		ApiError error = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message,
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, status);
	}
}
