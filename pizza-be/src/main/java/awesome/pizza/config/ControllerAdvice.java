package awesome.pizza.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException methodArgumentNotValidException) {
        StringBuilder errorBuilder = new StringBuilder();
        List<ObjectError> allValidationErrors = methodArgumentNotValidException.getBindingResult().getAllErrors();
        for (ObjectError validationError : allValidationErrors) {
            try {
                String field = ((FieldError) validationError).getField();
                errorBuilder.append(field).append(":").append(validationError.getDefaultMessage());
            } catch (Exception e) {
                log.warn("error handling error");
            }
            errorBuilder.append("\n");
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.INVALID_REQUEST.name());
        errorResponse.setErrorMessage(ErrorEnum.INVALID_REQUEST.getMessage());
        errorResponse.setDetailedMessage(errorBuilder.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.INVALID_REQUEST.name());
        errorResponse.setErrorMessage(ErrorEnum.INVALID_REQUEST.getMessage());
        errorResponse.setDetailedMessage(exception.getRootCause() != null
                ? exception.getRootCause().getMessage()
                : exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handle(HandlerMethodValidationException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.INVALID_REQUEST.name());
        errorResponse.setErrorMessage(ErrorEnum.INVALID_REQUEST.getMessage());
        String detailedMessage = exception.getAllValidationResults().stream().flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        errorResponse.setDetailedMessage(detailedMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handle(MissingServletRequestParameterException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.INVALID_REQUEST.name());
        errorResponse.setErrorMessage(ErrorEnum.INVALID_REQUEST.getMessage());
        errorResponse.setDetailedMessage("Missing required parameter: " + exception.getParameterName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException exception) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.DATA_INTEGRITY_VIOLATION.name());
        errorResponse.setErrorMessage(ErrorEnum.DATA_INTEGRITY_VIOLATION.getMessage());
        errorResponse.setDetailedMessage(exception.getRootCause() != null
                ? exception.getRootCause().getMessage()
                : exception.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AwesomePizzaException.class)
    public ResponseEntity<ErrorResponse> handle(AwesomePizzaException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorEnum.INTERNAL_ERROR.name());
        errorResponse.setErrorMessage(ErrorEnum.INTERNAL_ERROR.getMessage());
        errorResponse.setDetailedMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}