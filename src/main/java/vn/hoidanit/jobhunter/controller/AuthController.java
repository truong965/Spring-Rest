package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResponeLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
      private final AuthenticationManagerBuilder authenticationManagerBuilder;
      private final SecurityUtil securityUtil;
      private final UserService userService;
      @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
      private long refreshTokenExpiration;

      public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                  UserService userService) {
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.securityUtil = securityUtil;
            this.userService = userService;
      }

      @PostMapping("auth/login")
      public ResponseEntity<ResponeLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(), loginDTO.getPassword());
            // xac thuc nguoi dung > can viet override loadUserByUserName tại
            // userDetailCustom
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // thông tin về user được lưu trong principle của authentication nếu muốn thêm
            // thông tin trả về thì
            // phải override UserDetail của spring và thay đổi kiểu trả về tại hàm
            // loadUserByUserName (khá phức tạp)

            // set thông tin người dùng đăng nhập vào context để sử dụng sau này
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User currentUserDB = this.userService.findByEmail(loginDTO.getUsername());
            ResponeLoginDTO.UserLogin userLogin = new ResponeLoginDTO.UserLogin();
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            ResponeLoginDTO responeLoginDTO = new ResponeLoginDTO();
            responeLoginDTO.setUser(userLogin);
            // create token
            String accessToken = this.securityUtil.createAccessToken(authentication.getName(), responeLoginDTO);
            responeLoginDTO.setAccessToken(accessToken);
            // create refresh token
            String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), responeLoginDTO);
            // save refresh token
            this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

            // create cookie
            ResponseCookie responseCookie = ResponseCookie
                        .from("refresh_token", refreshToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(refreshTokenExpiration)
                        .build();

            return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .body(responeLoginDTO);
      }

      @GetMapping("/auth/account")
      @ApiMessage("fetch account")
      public ResponseEntity<ResponeLoginDTO.UserGetAccount> getAccount() {
            String email = SecurityUtil.getCurrentUserLogin().isPresent()
                        ? SecurityUtil.getCurrentUserLogin().get()
                        : "";
            User currentUserDB = this.userService.findByEmail(email);
            ResponeLoginDTO.UserLogin userLogin = new ResponeLoginDTO.UserLogin();
            ResponeLoginDTO.UserGetAccount userGetAccount = new ResponeLoginDTO.UserGetAccount();
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            userGetAccount.setUser(userLogin);
            return ResponseEntity.ok().body(userGetAccount);
      }

      @GetMapping("/auth/refresh")
      @ApiMessage("Get user by refresh token")
      public ResponseEntity<ResponeLoginDTO> getRefreshToken(
                  @CookieValue(name = "refresh_token", defaultValue = "abc") String refreshToken)
                  throws InvalidException {
            if (refreshToken.equals("abc")) {
                  throw new InvalidException("you dont have refresh token at cookies");
            }
            // get refresh token from cookie
            // check valid refresh token
            Jwt decodeToken = securityUtil.checkValidRefreshToken(refreshToken);
            String email = decodeToken.getSubject();
            // check user by token + email
            User exUser = userService.getUserByEmailAndRefreshToken(email, refreshToken);
            if (exUser == null) {
                  throw new InvalidException("invalid refresh token ");
            }

            // create new token and set refresh token as cookies

            User currentUserDB = this.userService.findByEmail(email);
            ResponeLoginDTO.UserLogin userLogin = new ResponeLoginDTO.UserLogin();
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());
            ResponeLoginDTO responeLoginDTO = new ResponeLoginDTO();
            responeLoginDTO.setUser(userLogin);
            // create token
            String accessToken = this.securityUtil.createAccessToken(email, responeLoginDTO);
            responeLoginDTO.setAccessToken(accessToken);
            // create refresh token
            String newRefreshToken = this.securityUtil.createRefreshToken(email, responeLoginDTO);
            // save refresh token
            this.userService.updateUserToken(newRefreshToken, email);

            // create cookie
            ResponseCookie responseCookie = ResponseCookie
                        .from("refresh_token", newRefreshToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(refreshTokenExpiration)
                        .build();

            return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .body(responeLoginDTO);
      }

      @PostMapping("/auth/logout")
      @ApiMessage("logout")
      public ResponseEntity<Void> hanldeLogout() throws InvalidException {
            // set refresh token = null at database
            String email = SecurityUtil.getCurrentUserLogin().isPresent()
                        ? SecurityUtil.getCurrentUserLogin().get()
                        : "";
            if (email.isEmpty()) {
                  throw new InvalidException("invalid access token ");
            }
            userService.updateUserToken(null, email);
            // remove refreshToken at cookies
            ResponseCookie deleteResponseCookie = ResponseCookie
                        .from("refresh_token", null)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .build();
            return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, deleteResponseCookie.toString())
                        .body(null);
      }

}
