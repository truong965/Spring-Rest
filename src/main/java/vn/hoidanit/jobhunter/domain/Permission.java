package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "permissions")
public class Permission {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "permission_id")
      private long id;
      @NotBlank(message = "name is not empty")
      private String name;
      @NotBlank(message = "apiPath is not empty")
      private String apiPath;
      @NotBlank(message = "method is not empty")
      private String method;
      @NotBlank(message = "module is not empty")
      private String module;

      private Instant createdAt;
      private Instant updatedAt;
      private String createdBy;
      private String updatedBy;

      @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
      @JsonIgnore
      private List<Role> roles;

      public Permission(String name, String apiPath, String method, String module) {
            this.name = name;
            this.apiPath = apiPath;
            this.method = method;
            this.module = module;
      }

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
