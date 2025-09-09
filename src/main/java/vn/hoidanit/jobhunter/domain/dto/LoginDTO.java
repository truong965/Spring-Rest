package vn.hoidanit.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
      @NotBlank(message = "username khong duoc de trong")
      private String username;
      @NotBlank(message = "password khong duoc de trong")
      private String password;

}
