package vn.hoidanit.jobhunter.domain;

import java.util.List;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "subscribers")
public class Subscriber {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "subscriber_id")
      private long id;
      private String name;
      private String email;

      @ManyToMany(fetch = FetchType.LAZY)
      @JsonIgnoreProperties(value = { "subscribers" })
      @JoinTable(name = "subscriber_skill", joinColumns = @JoinColumn(name = "subscriber_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
      private List<Skill> skills;
}
