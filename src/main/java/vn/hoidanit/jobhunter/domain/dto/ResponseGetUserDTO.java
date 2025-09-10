package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGetUserDTO {
      private long id;
      private String name;
      private String email;
      private int age;
      private GenderEnum gender;
      private String address;
      private Instant createAt;
      private Instant updateAt;
      private String createdBy;
      private String updatedBy;

}