package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Controller
@RequestMapping("/api/v1")
public class JobController {
      private final JobService jobService;

      public JobController(JobService jobService) {
            this.jobService = jobService;
      }

      @PostMapping("/jobs")
      @ApiMessage("create job")
      public ResponseEntity<ResponseCreateJobDTO> handleCreateJob(@Valid @RequestBody Job job) {
            ResponseCreateJobDTO newJob = this.jobService.createJob(job);
            return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
      }

      @PutMapping("/jobs")
      @ApiMessage("update job")
      public ResponseEntity<ResponseCreateJobDTO> handleUpdateJob(@Valid @RequestBody Job job) throws InvalidException {
            // TODO: process PUT request
            Job currentJob = this.jobService.fetchJobById(job.getId());
            if (currentJob == null) {
                  throw new InvalidException("jon id =" + job.getId() + " is not exists");
            }

            return ResponseEntity.ok(this.jobService.updateJob(job));
      }

      @DeleteMapping("/jobs/{id}")
      @ApiMessage("delete job")
      public ResponseEntity<Void> handleDelteJob(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            Job currentJob = this.jobService.fetchJobById(id);
            if (currentJob == null) {
                  throw new InvalidException("job id =" + id + " is not exists");
            }

            this.jobService.deleteJob(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/jobs")
      @ApiMessage("fetch all Job")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllJob(@Filter Specification<Job> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(jobService.fetchAllJob(specification, pageable));
      }

      @GetMapping("/jobs/{id}")
      @ApiMessage("fetch Job by id")
      public ResponseEntity<Job> handleFetchJobById(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            Job currentJob = this.jobService.fetchJobById(id);
            if (currentJob == null) {
                  throw new InvalidException("job id =" + id + " is not exists");
            }
            return ResponseEntity.ok(jobService.fetchJobById(id));
      }

}
