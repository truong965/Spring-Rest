package vn.hoidanit.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResponeLoginDTO;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
      private final AuthenticationManagerBuilder authenticationManagerBuilder;
      private final SecurityUtil securityUtil;

      public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.securityUtil = securityUtil;
      }

      @PostMapping("/login")
      public ResponseEntity<ResponeLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(), loginDTO.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // create token

            String accessToken = this.securityUtil.createToken(authentication);
            // this is redundant
            SecurityContextHolder.getContext().setAuthentication(authentication);
            ResponeLoginDTO responeLoginDTO = new ResponeLoginDTO(accessToken);
            return ResponseEntity.ok(responeLoginDTO);
      }
}
