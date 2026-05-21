package com.seleebe.youowe.exception;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponseDto handleNotFound(ResourceNotFoundException ex) {
    return new ErrorResponseDto(ex.getMessage());
  }

  @ExceptionHandler(ActiveDebtException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponseDto handleActiveDebt(ActiveDebtException ex) {
    return new ErrorResponseDto(ex.getMessage());
  }

  @ExceptionHandler(InvalidBusinessOperationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleInvalidOperation(InvalidBusinessOperationException ex) {
    return new ErrorResponseDto(ex.getMessage());
  }

  @ExceptionHandler(NoAccessException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponseDto handleNoAccessException(NoAccessException ex) {
    return new ErrorResponseDto(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponseDto handleAllOtherExceptions(Exception ex) {
    return new ErrorResponseDto("Internal server error: " + ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleValidationExceptions(MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));

    return new ErrorResponseDto(errorMessage);
  }
}
