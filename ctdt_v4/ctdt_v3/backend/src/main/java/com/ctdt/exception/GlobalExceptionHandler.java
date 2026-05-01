package com.ctdt.exception;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler; import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime; import java.util.LinkedHashMap; import java.util.Map;
import java.util.stream.Collectors;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e){return err(HttpStatus.NOT_FOUND,e.getMessage());}
    @ExceptionHandler(BadRequestException.class) public ResponseEntity<Map<String,Object>> bad(BadRequestException e){return err(HttpStatus.BAD_REQUEST,e.getMessage());}
    @ExceptionHandler(RecalculationLockException.class) public ResponseEntity<Map<String,Object>> lock(RecalculationLockException e){return err(HttpStatus.TOO_MANY_REQUESTS,e.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> val(MethodArgumentNotValidException e){
        String msg=e.getBindingResult().getFieldErrors().stream().map(fe->fe.getField()+": "+fe.getDefaultMessage()).collect(Collectors.joining("; "));
        return err(HttpStatus.BAD_REQUEST,msg);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> gen(Exception e){

        e.printStackTrace();   // prints the real error in terminal

        return err(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }    private ResponseEntity<Map<String,Object>> err(HttpStatus s,String msg){
        Map<String,Object> b=new LinkedHashMap<>();b.put("timestamp",LocalDateTime.now().toString());b.put("status",s.value());b.put("error",s.getReasonPhrase());b.put("message",msg);
        return ResponseEntity.status(s).body(b);
    }
}
