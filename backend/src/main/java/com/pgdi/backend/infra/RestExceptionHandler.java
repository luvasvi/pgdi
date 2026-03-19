package com.pgdi.backend.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleValidationException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Unprocessable Entity");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY); // Retorna 422
    }

    // Tratamento para Recursos Não Encontrados (404)
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", "O recurso solicitado não foi encontrado.");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Erros gerais de Runtime (400)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}