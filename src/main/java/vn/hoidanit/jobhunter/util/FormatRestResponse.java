package vn.hoidanit.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.Response.RestResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

      @Override
      public boolean supports(MethodParameter returnType, Class converterType) {
            // TODO Auto-generated method stub
            return true;
      }

      @Override
      public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();
            int status = res.getStatus();
            if (body instanceof String) {
                  return body;
            }
            // case error
            RestResponse<Object> rs = new RestResponse<Object>();
            rs.setStatusCode(status);
            if (status >= 400) {
                  return body;
            }
            rs.setData(body);
            rs.setMessage("success");
            return rs;
      }

}
