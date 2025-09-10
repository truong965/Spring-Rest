package vn.hoidanit.jobhunter.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.response.RestResponse;

// handle exception to show message at body 
@Component
public class CustomAutheticationEntryPoint implements AuthenticationEntryPoint {

      private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
      // convert data to object
      private final ObjectMapper mapper;

      public CustomAutheticationEntryPoint(ObjectMapper mapper) {
            this.mapper = mapper;
      }

      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response,
                  AuthenticationException authException) throws IOException, ServletException {
            // TODO Auto-generated method stub
            this.delegate.commence(request, response, authException);
            // support vietnamese
            response.setContentType("application/json;charset=UTF-8");
            //
            RestResponse<Object> res = new RestResponse<Object>();
            res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            // handle when don't have token
            String errorMessage = Optional.ofNullable(authException.getCause()).map(Throwable::getMessage)
                        .orElse(authException.getMessage());

            res.setError(errorMessage);
            res.setMessage("Token không hợp lệ");
            mapper.writeValue(response.getWriter(), res);
      }

}
