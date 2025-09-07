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
import vn.hoidanit.jobhunter.domain.dto.LoginDTO.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername ->>>> Khi người dùng
        // login bằng username/password.
        // Kiểm tra username/password → Tạo Authentication.
        // Lưu Authentication vào SecurityContext
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create a token
        ResLoginDTO res = new ResLoginDTO();
        User currentUser = this.userService.fetchUserByEmail(loginDTO.getUsername());
        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                    currentUser.getName(), currentUser.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);

        res.setAccessToken(access_token);

        // create refresh token and update user
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        this.userService.handleSaveToken(refresh_token, loginDTO.getUsername());

        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = this.userService.fetchUserByEmail(email);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(email);
            userLogin.setName(currentUser.getName());
            userLogin.setRole(currentUser.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "default") String refresh_token)
            throws IdInvalidException {
        // check valid
        Jwt jwtDecoded = this.securityUtil.checkValidToken(refresh_token);
        String email = jwtDecoded.getSubject();
        // check user by token + email
        User currentUser = this.userService.fetchUserByRefreshTokenAndEmail(email, refresh_token);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }
        // create a token
        ResLoginDTO res = new ResLoginDTO();
        User currentUser2 = this.userService.fetchUserByEmail(email);
        if (currentUser2 != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser2.getId(), currentUser2.getEmail(),
                    currentUser2.getName(), currentUser2.getRole());
            res.setUser(userLogin);
        }

        String acces_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(acces_token);

        // create refresh token and update user
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.handleSaveToken(new_refresh_token, email);

        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("user logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email == "") {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        this.userService.handleSaveToken(null, email);
        // delete cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }
}
