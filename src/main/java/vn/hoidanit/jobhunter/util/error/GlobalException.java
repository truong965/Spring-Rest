package vn.hoidanit.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

@RestControllerAdvice // ResponseBody + ControllerAdvice
public class GlobalException {

    // BadCredentials will be stop at filter before go to controller at here
    //
    @ExceptionHandler(value = {
            InvalidException.class,
            UsernameNotFoundException.class,
            BadCredentialsException.class })

    public ResponseEntity<RestResponse<Object>> handleInvalidException(Exception ex) {
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
        rs.setMessage(ex.getMessage());
        rs.setError("exception occurs ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class })

    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(HttpStatus.NOT_FOUND.value());
        rs.setMessage(ex.getMessage());
        rs.setError("404 NOT FOUND !!! url may not exists");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    // handle valid exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ma) {
        BindingResult result = ma.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        // if have 1 error , return string and return list if it have more than 1
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ma.getBody().getDetail());
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.getFirst());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            StorageException.class })

    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex) {
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
        rs.setMessage(ex.getMessage());
        rs.setError("exception upload file occurs ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RestResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE.value());
        res.setMessage(ex.getMessage());
        res.setError("File size exceeds the configured limit.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(res);
    }

    @ExceptionHandler(value = {
            PermissionException.class })

    public ResponseEntity<RestResponse<Object>> handlePermisstionException(Exception ex) {
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(HttpStatus.FORBIDDEN.value());
        rs.setMessage(ex.getMessage());
        rs.setError("forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(rs);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}