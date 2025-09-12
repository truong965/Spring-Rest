package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "companies")
public class Company {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "company_id")
      private long id;
      @NotBlank(message = "khong duoc de trong")
      private String name;
      @Column(columnDefinition = "MEDIUMTEXT")
      private String description;
      private String address;
      private String logo;

      private Instant createdAt;
      private Instant updatedAt;
      private String createdBy;
      private String updatedBy;

      @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
      @JsonIgnore
      private List<User> users;

      @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
      @JsonIgnore
      private List<Job> jobs;

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
