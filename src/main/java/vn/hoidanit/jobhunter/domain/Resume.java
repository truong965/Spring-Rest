package vn.hoidanit.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.constant.ResumeStatusEnum;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resumes")
public class Resume {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "resume_id")
      private long id;
      @NotBlank(message = "email is not empty")
      private String email;
      @NotBlank(message = "url is not empty")
      private String url;

      @Enumerated(EnumType.STRING)
      private ResumeStatusEnum status;

      private Instant createdAt;
      private Instant updatedAt;
      private String createdBy;
      private String updatedBy;

      @ManyToOne
      @JoinColumn(name = "user_id")
      private User user;

      @ManyToOne
      @JoinColumn(name = "job_id")
      private Job job;

      @PrePersist
      public void handlePrePersist() {
            this.createdAt = Instant.now();
            this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                        ? SecurityUtil.getCurrentUserLogin().get()
                        : "";
      }

      @PreUpdate
      public void handlePreUpdate() {
            this.updatedAt = Instant.now();
            this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                        ? SecurityUtil.getCurrentUserLogin().get()
                        : "";
      }

}
