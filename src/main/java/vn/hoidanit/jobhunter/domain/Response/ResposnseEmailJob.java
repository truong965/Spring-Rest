package vn.hoidanit.jobhunter.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResposnseEmailJob {
      private String name;
      private double salary;
      private CompanyEmail company;
      private List<SkillEmail> skills;

      @Getter
      @Setter
      @AllArgsConstructor
      @NoArgsConstructor
      public static class CompanyEmail {
            private String name;
      }

      @Getter
      @Setter
      @AllArgsConstructor
      @NoArgsConstructor
      public static class SkillEmail {
            private String name;
      }
}
