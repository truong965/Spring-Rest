package vn.hoidanit.jobhunter.domain.dto.UserDTO;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Setter
@Getter
public class ResCreatedUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private String createdBy;

    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
