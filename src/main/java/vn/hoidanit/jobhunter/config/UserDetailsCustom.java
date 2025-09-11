package vn.hoidanit.jobhunter.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import vn.hoidanit.jobhunter.service.UserService;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {
      private final UserService userService;

      public UserDetailsCustom(UserService userService) {
            this.userService = userService;
      }

      // mục đích chính của việc ghi đè phương thức loadUserByUsername là để chỉ cho
      // Spring Security cách tìm và lấy thông tin người dùng (như username, password,
      // quyền hạn) từ nguồn dữ liệu của riêng bạ
      // Do đó, Spring Security định nghĩa ra một "hợp đồng" (contract) thông qua
      // interface UserDetailsService. Bất kỳ ai muốn tích hợp hệ thống lưu trữ người
      // dùng của mình với Spring Security đều phải tuân theo hợp đồng này.
      // mặc định sẽ gọi tớiDaoAuthenticationProvider để lấy loadUserByUsername nhưng
      // bây giờ chúng ta đã implement và override nên nó sẽ đi vào đây để xử lý
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            vn.hoidanit.jobhunter.domain.User user = this.userService.findByEmail(username);
            if (user == null) {
                  throw new UsernameNotFoundException("invalid username or password");
            }

            return new User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
      }
}
