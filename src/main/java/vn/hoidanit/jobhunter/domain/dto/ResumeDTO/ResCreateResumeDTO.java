package vn.hoidanit.jobhunter.domain.dto.ResumeDTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}