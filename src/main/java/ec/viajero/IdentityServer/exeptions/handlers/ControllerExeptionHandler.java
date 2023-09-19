package ec.viajero.IdentityServer.exeptions.handlers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ec.viajero.IdentityServer.exeptions.LoginException;
import ec.viajero.IdentityServer.exeptions.BadRequestAlertException;
import ec.viajero.IdentityServer.exeptions.CustomError;
import ec.viajero.IdentityServer.exeptions.EmailAlreadyUsedException;
import ec.viajero.IdentityServer.exeptions.EmailTemplateNotFoundByIdException;
import ec.viajero.IdentityServer.exeptions.EmailTemplateNotFoundByTypeException;
import ec.viajero.IdentityServer.exeptions.ErrorCodes;
import ec.viajero.IdentityServer.exeptions.ErrorDetails;
import ec.viajero.IdentityServer.exeptions.InvalidPasswordException;
import ec.viajero.IdentityServer.exeptions.UserNotActivatedException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByActivationKeyException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByResetKeyException;
import ec.viajero.IdentityServer.exeptions.UserNotFoundByUserNameException;
import ec.viajero.IdentityServer.exeptions.UserWasBlockedException;
import ec.viajero.IdentityServer.exeptions.UsernameAlreadyUsedException;
import lombok.extern.slf4j.Slf4j;

/**
 * Plase if you have problem with controller exeption handler
 * see this https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-advice.html
 **/

@Slf4j
@ControllerAdvice
public class ControllerExeptionHandler {
    

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> handleExeption(LoginException ex) {
        log.error("LoginException was throw with message >> {} and error {}", ex.getMessage(), ex.getError().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getError());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleExeption(BadCredentialsException ex) {
        CustomError error = CustomError.builder().traceId(UUID.randomUUID().toString())
                             .status(HttpStatus.UNAUTHORIZED)
                             .timestamp(new Date().getTime())
                             .errors(List.of(new ErrorDetails(ErrorCodes.INVALID_CREDENCIALS_EC,"Bad credencial","https//error/details#123")))
                             .build();
        log.error("BadCredentialsException was throw with message >> {} and error {}", ex.getMessage(),error.toString());         
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error);

    }

    

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleExeption(InvalidPasswordException ex) {
        log.error("InvalidPasswordException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getError());
    }

    @ExceptionHandler(UserNotFoundByResetKeyException.class)
    public ResponseEntity<?> handleExeption(UserNotFoundByResetKeyException ex) {
        log.error("UserNotFoundByResetKeyException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getError());
    }

    @ExceptionHandler(UserNotFoundByActivationKeyException.class)
    public ResponseEntity<?> handleExeption(UserNotFoundByActivationKeyException ex) {
        log.error("UserNotFoundByActivationKeyException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getError());

    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<?> handleExeption(UsernameAlreadyUsedException ex) {
        log.error("UsernameAlreadyUsedException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getError());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<?> handleExeption(EmailAlreadyUsedException ex) {
        log.error("EmailAlreadyUsedException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getError());
    }

    @ExceptionHandler(EmailTemplateNotFoundByIdException.class)
    public ResponseEntity<?> handleExeption(EmailTemplateNotFoundByIdException ex) {
        log.error("EmailTemplateNotFoundByIdException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getError());
    }

    @ExceptionHandler(EmailTemplateNotFoundByTypeException.class)
    public ResponseEntity<?> handleExeption(EmailTemplateNotFoundByTypeException ex) {
        log.error("EmailTemplateNotFoundByTypeException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getError());
    }

    @ExceptionHandler(UserNotActivatedException.class)
     public ResponseEntity<?> handleExeption(UserNotActivatedException ex) {
        log.error("UserNotActivatedException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getError());
    }

    @ExceptionHandler(UserNotFoundByUserNameException.class)
     public ResponseEntity<?> handleExeption(UserNotFoundByUserNameException ex) {
        log.error("UserNotFoundByUserNameException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getError());
    }

    @ExceptionHandler(UserWasBlockedException.class)
     public ResponseEntity<?> handleExeption(UserWasBlockedException ex) {
        log.error("UserWasBlockedException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getError());
    }

     @ExceptionHandler(BadRequestAlertException.class)
     public ResponseEntity<?> handleExeption(BadRequestAlertException ex) {
        log.error("BadRequestAlertException was throw with message >> {} and error {}", ex.getMessage(),ex.getError().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getError());
    }

}