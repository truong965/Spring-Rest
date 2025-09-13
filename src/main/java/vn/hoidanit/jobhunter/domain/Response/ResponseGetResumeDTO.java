package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.constant.ResumeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGetResumeDTO {
      private long id;
      private String email;
      private String url;
      private ResumeStatusEnum status;

      private Instant createdAt;
      private Instant updatedAt;
      private String createdBy;
      private String updatedBy;
      private String companyName;
      private ResponseResumeUser user;
      private ResponseResumeJob job;
}
