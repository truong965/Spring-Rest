package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
      private SkillService skillService;

      public SkillController(SkillService skillService) {
            this.skillService = skillService;
      }

      @PostMapping("/skills")
      @ApiMessage("create skill")
      public ResponseEntity<Skill> handleCreateSkill(@Valid @RequestBody Skill skill) throws InvalidException {
            if (skillService.isNaneExists(skill.getName())) {
                  throw new InvalidException("skill name =" + skill.getName() + " is exists");
            }
            Skill newSkill = this.skillService.createSkill(skill);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
      }

      @PutMapping("/skills")
      @ApiMessage("update skill")
      public ResponseEntity<Skill> handleUpdateSkill(@Valid @RequestBody Skill skill) throws InvalidException {

            Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
            if (currentSkill == null) {
                  throw new InvalidException("skill id =" + skill.getId() + " is not exists");
            }

            if (skillService.isNaneExists(skill.getName())) {
                  throw new InvalidException("skill name =" + skill.getName() + " is exists");
            }
            return ResponseEntity.ok(this.skillService.updateSkill(skill));
      }

      @DeleteMapping("/skills/{id}")
      @ApiMessage("delete skill")
      public ResponseEntity<Void> handleDeleteSkill(@PathVariable("id") Long id) throws InvalidException {
            Skill currentSkill = this.skillService.fetchSkillById(id);
            if (currentSkill == null) {
                  throw new InvalidException("skill id =" + id + " is not exists");
            }

            this.skillService.deleteSkill(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/skills")
      @ApiMessage("fetch all Skill")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllSkill(@Filter Specification<Skill> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(skillService.fetchAllSkill(specification, pageable));
      }

      @GetMapping("/skills/{id}")
      @ApiMessage("get skill by id")
      public ResponseEntity<Skill> handleFetchSkillById(@PathVariable("id") Long id) throws InvalidException {
            Skill currentSkill = this.skillService.fetchSkillById(id);
            if (currentSkill == null) {
                  throw new InvalidException("skill id =" + id + " is not exists");
            }
            return ResponseEntity.ok(skillService.fetchSkillById(id));
      }
}
