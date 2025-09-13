package vn.hoidanit.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.InvalidException;
import vn.hoidanit.jobhunter.util.error.PermissionException;

// Request => Spring Security => Interceptor => Controller => Service...
// duoc dung de kiem tra quyen truy cap
// chi can return true thi moi duoc di tiep
public class PermissionInterceptor implements HandlerInterceptor {
      @Autowired
      private UserService userService;

      @Override
      @Transactional // method này thực hiện trước khi tới được controller và service vì vậy sẽ gặp
                     // lỗi no session
      // thêm annotation này để báo với Spring boot tạo session khi thực hiện query
      // xuống database để lấy user , sau đó session sẽ tự động đóng
      public boolean preHandle(
                  HttpServletRequest request,
                  HttpServletResponse response, Object handler)
                  throws Exception, PermissionException {
            String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String requestURI = request.getRequestURI();
            String httpMethod = request.getMethod();
            System.out.println(">>> RUN preHandle");
            System.out.println(">>> path= " + path);
            System.out.println(">>> httpMethod= " + httpMethod);
            System.out.println(">>> requestURI= " + requestURI);

            // check permission
            String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                        : "";
            if (email != null && !email.isEmpty()) {
                  User user = this.userService.findByEmail(email);
                  if (user != null) {
                        Role role = user.getRole();
                        if (role != null) {
                              List<Permission> permissions = role.getPermissions();
                              boolean isAllow = permissions.stream()
                                          .anyMatch(p -> p.getApiPath().equalsIgnoreCase(requestURI));
                              if (isAllow) {
                                    return true;
                              }
                              throw new PermissionException("you don't have permission to access this endpoint");
                        }
                  }
                  throw new PermissionException("you don't have permission to access this endpoint");
            }
            throw new PermissionException("you don't have permission to access this endpoint");

      }
}