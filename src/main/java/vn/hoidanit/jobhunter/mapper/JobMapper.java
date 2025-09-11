package vn.hoidanit.jobhunter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateJobDTO;

@Mapper(componentModel = "spring")
public interface JobMapper {
      // Chỉ định rõ: dùng phương thức có tên "skillToName" để xử lý skills
      @Mapping(source = "skills", target = "skills", qualifiedByName = "skillToName")
      ResponseCreateJobDTO toResponseCreateJobDTO(Job job);

      // Phương thức phụ để xử lý mapping Skill
      // Dạy MapStruct cách chuyển một Skill thành một String
      @Named("skillToName")
      default String skillToName(Skill skill) {
            if (skill == null) {
                  return null;
            }
            return skill.getName();
      }
}