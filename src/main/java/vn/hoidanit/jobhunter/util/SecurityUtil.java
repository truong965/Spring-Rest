package vn.hoidanit.jobhunter.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

import vn.hoidanit.jobhunter.domain.response.ResponeLoginDTO;

@Service
public class SecurityUtil {
      private final JwtEncoder jwtEncoder;
      public static final String AUTHORITIES_KEY = "user";

      public SecurityUtil(JwtEncoder jwtEncoder) {
            this.jwtEncoder = jwtEncoder;
      }

      public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

      @Value("${hoidanit.jwt.base64-secret}")
      private String jwtKey;
      @Value("${hoidanit.jwt.access-token-validity-in-seconds}")
      private long accessTokenExpiration;
      @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
      private long refreshTokenExpiration;

      public String createAccessToken(String email, ResponeLoginDTO rep) {
            ResponeLoginDTO.UserInsideToken userToken = new ResponeLoginDTO.UserInsideToken();
            userToken.setId(rep.getUser().getId());
            userToken.setEmail(rep.getUser().getEmail());
            userToken.setName(rep.getUser().getName());

            Instant now = Instant.now();
            Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
            List<String> listAuthority = new ArrayList<>();
            listAuthority.add("ROLE_USER_CREATE");
            listAuthority.add("ROLE_USER_UPDATE");

            // @formatter:off
            JwtClaimsSet claims = JwtClaimsSet.builder()
                  .issuedAt(now)
                  .expiresAt(validity)
                  .subject(email)
                  .claim(AUTHORITIES_KEY, userToken)
                  .claim("premission", listAuthority)
                  .build();
            JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
            return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
      }

         public String createRefreshToken(String email, ResponeLoginDTO rep) {
                       ResponeLoginDTO.UserInsideToken userToken = new ResponeLoginDTO.UserInsideToken();
            userToken.setId(rep.getUser().getId());
            userToken.setEmail(rep.getUser().getEmail());
            userToken.setName(rep.getUser().getName());

            Instant now = Instant.now();
            Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

            // @formatter:off
            JwtClaimsSet claims = JwtClaimsSet.builder()
                  .issuedAt(now)
                  .expiresAt(validity)
                  .subject(email)
                  .claim(AUTHORITIES_KEY,userToken)
                  .build();
            JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
            return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
      }
      public static Optional<String> getCurrentUserLogin(){
            SecurityContext securityContext = SecurityContextHolder.getContext();
            return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
      }
      private static String extractPrincipal (Authentication authentication){
            if(authentication ==null){
                  return null;
            }else if(authentication.getPrincipal() instanceof UserDetails springSecurityUser){
                  return springSecurityUser.getUsername();
            }else if(authentication.getPrincipal() instanceof Jwt jwt){
                  return jwt.getSubject();
            }else if(authentication.getPrincipal() instanceof String s){
                  return s;
            }
            return null;
      }

      public Jwt checkValidRefreshToken(String token){
             NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                        getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
                try {
                         return jwtDecoder.decode(token);
                  } catch (Exception e) {
                        System.out.println(">>> JWT error: " + e.getMessage());
                        throw e;
                  }

      }
        private SecretKey getSecretKey() {
            byte[] keyBytes = Base64.from(jwtKey).decode();
            return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
      }
      // public static Optional<String> getCurrentUserJWT(){
      //       SecurityContext securityContext= SecurityContextHolder.getContext();
      //       return Optional.ofNullable(securityContext.getAuthentication())
      //       .filter(au-> au.getCredentials() instanceof String)
      //       .map(au-> (String)au.getCredentials());
      // }
      // public static boolean isAuthenicated(){
      //       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      //       return authentication!=null && getAuthorities(authentication).noneMatch(Au)
      // }
      // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities){
      //       Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
      //       return(authentication!=null && getAuthorities(authentication).anyMatch(au->Arrays.asList(authorities).contains(au)));
      // }
      // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities){
      //       return !hasCurrentUserAnyOfAuthorities(authorities);
      // }
      // public static boolean hasCurrentUserThisAuthorities(String authority){
      //       return hasCurrentUserAnyOfAuthorities(authority);
      // }
      // private static Stream<String> getAuthorities(Authentication authentication){
      //       return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
      // }
}
