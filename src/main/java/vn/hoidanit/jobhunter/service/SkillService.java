package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
      private final SkillRepository skillRepository;

      public SkillService(SkillRepository skillRepository) {
            this.skillRepository = skillRepository;
      }

      public boolean isNaneExists(String name) {
            return this.skillRepository.existsByName(name);
      }

      @Transactional
      public Skill createSkill(Skill skill) {
            return this.skillRepository.save(skill);
      }

      @Transactional
      public void deleteSkill(Long id) {
            Skill currentSkill = fetchSkillById(id);
            if (currentSkill != null) {
                  currentSkill.getJobs().forEach(j -> j.getSkills().remove(currentSkill));
                   // delete subscriber_skill
                  currentSkill.getSubscribers().forEach(s -> s.getSkills().remove(currentSkill));
            }
            this.skillRepository.deleteById(id);
      }

      @Transactional
      public Skill updateSkill(Skill skill) {
            Optional<Skill> ec = this.skillRepository.findById(skill.getId());
            if (!ec.isPresent())
                  return null;
            Skill existSkill = ec.get();
            existSkill.setName(skill.getName());
            return this.skillRepository.save(existSkill);
      }

      public Skill fetchSkillById(Long id) {
            return this.skillRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllSkill(Specification<Skill> specification, Pageable pageable) {

            Page<Skill> pageSkill = this.skillRepository.findAll(specification, pageable);
            ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(pageSkill.getTotalPages());
            meta.setTotalPages(pageSkill.getTotalElements());

            resultPaginationDTO.setMeta(meta);
            resultPaginationDTO.setResult(pageSkill.getContent());
            return resultPaginationDTO;
      }

      public List<Skill> fetchSkillsByIds(List<Long> ids) {
            return this.skillRepository.findAllById(ids);
      }
}
