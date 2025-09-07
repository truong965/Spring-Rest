package vn.hoidanit.jobhunter.domain.dto.UserDTO;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    private CompanyUser company;
    private RoleUser role;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String name;
    }
}
