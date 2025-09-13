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
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseUpdatedResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
      private final ResumeService resumeService;
      private final JobService jobService;
      private final UserService userService;

      public ResumeController(ResumeService resumeService, JobService jobService, UserService userService) {
            this.resumeService = resumeService;
            this.jobService = jobService;
            this.userService = userService;
      }

      @PostMapping("/resumes")
      @ApiMessage("create resume")
      public ResponseEntity<ResponseCreateResumeDTO> handleCreateResume(@Valid @RequestBody Resume resume)
                  throws InvalidException {
            // check job and user is exists
            if (resume.getJob() == null || this.jobService.fetchJobById(resume.getJob().getId()) == null) {
                  throw new InvalidException("job/user id is not exists");
            }
            if (resume.getUser() == null || this.userService.findById(resume.getUser().getId()) == null) {
                  throw new InvalidException("job/user id is not exists");
            }
            ResponseCreateResumeDTO newResume = this.resumeService.createResume(resume);
            return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
      }

      @PutMapping("/resumes")
      @ApiMessage("update resume")
      public ResponseEntity<ResponseUpdatedResumeDTO> handleUpdateResume(@RequestBody Resume resume)
                  throws InvalidException {
            // TODO: process PUT request
            ResponseGetResumeDTO currentResume = this.resumeService.fetchResumeById(resume.getId());
            if (currentResume == null) {
                  throw new InvalidException("resume id =" + resume.getId() + " is not exists");
            }
            return ResponseEntity.ok(this.resumeService.updateResume(resume));
      }

      @DeleteMapping("/resumes/{id}")
      @ApiMessage("delete resume")
      public ResponseEntity<Void> handleDelteResume(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            ResponseGetResumeDTO currentResume = this.resumeService.fetchResumeById(id);
            if (currentResume == null) {
                  throw new InvalidException("resume id =" + id + " is not exists");
            }

            this.resumeService.deleteResume(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/resumes/{id}")
      @ApiMessage("fetch resume by id")
      public ResponseEntity<ResponseGetResumeDTO> handleFetchResumeById(@PathVariable("id") Long id)
                  throws InvalidException {
            // TODO: process PUT request
            ResponseGetResumeDTO currentResume = this.resumeService.fetchResumeById(id);
            if (currentResume == null) {
                  throw new InvalidException("resume id =" + id + " is not exists");
            }

            return ResponseEntity.ok(resumeService.fetchResumeById(id));
      }

      @GetMapping("/resumes")
      @ApiMessage("fetch all resume")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllResume(@Filter Specification<Resume> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(resumeService.fetchAllResume(specification, pageable));
      }

      @PostMapping("/resumes/by-user")
      @ApiMessage("fetch all resume by user")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllResumeByUser(
                  Pageable pageable) {
            return ResponseEntity.ok(resumeService.fetchAllResumeByUser(pageable));
      }

}
