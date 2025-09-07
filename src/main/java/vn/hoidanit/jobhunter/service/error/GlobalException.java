package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.Response.RestResponse;

@RestControllerAdvice // ResponseBody + ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(IdInvalidException ex) {
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(HttpStatus.BAD_REQUEST.value());
        rs.setError(ex.getMessage());
        rs.setMessage("idInvalidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
}