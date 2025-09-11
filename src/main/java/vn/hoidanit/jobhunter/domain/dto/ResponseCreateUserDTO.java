package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCreateUserDTO {
      private long id;
      private String name;
      private String email;
      private int age;
      private GenderEnum gender;
      private String address;
      private Instant createdAt;
}
