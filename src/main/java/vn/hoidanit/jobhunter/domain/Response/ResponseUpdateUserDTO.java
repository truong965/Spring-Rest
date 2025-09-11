package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;
import vn.hoidanit.jobhunter.domain.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUpdateUserDTO {
      private long id;
      private String name;
      private int age;
      private GenderEnum gender;
      private String address;
      private Instant updatedAt;
}
