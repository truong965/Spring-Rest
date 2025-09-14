package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "roles")
public class Role {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "role_id")
      private long id;
      private String name;
      @Column(columnDefinition = "MEDIUMTEXT")
      private String description;
      private boolean active;

      private Instant createdAt;
      private Instant updatedAt;
      private String createdBy;
      private String updatedBy;

      @ManyToMany(fetch = FetchType.LAZY)
      @JsonIgnoreProperties(value = { "roles", "users" })
      @JoinTable(name = "permission_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
      private List<Permission> permissions;

      @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
      @JsonIgnore
      private List<User> users;

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
