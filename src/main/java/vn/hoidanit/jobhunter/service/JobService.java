package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.mapper.JobMapper;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
      private final JobRepository jobRepository;
      private final SkillRepository skillRepository;
      private final JobMapper jobMapper;

      public JobService(JobRepository jobRepository, SkillRepository skillRepository, JobMapper jobMapper) {
            this.jobRepository = jobRepository;
            this.skillRepository = skillRepository;
            this.jobMapper = jobMapper;
      }

      @Transactional
      public ResponseCreateJobDTO createJob(Job job) {
            // check skill
            if (job.getSkills() != null) {
                  List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
                  List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
                  job.setSkills(dbSkills);
            }
            Job newJob = this.jobRepository.save(job);
            return jobMapper.toResponseCreateJobDTO(newJob);
      }

      @Transactional
      public void deleteJob(Long id) {
            this.jobRepository.deleteById(id);
      }

      @Transactional
      public ResponseCreateJobDTO updateJob(Job job) {
            Optional<Job> ec = this.jobRepository.findById(job.getId());
            if (!ec.isPresent())
                  return null;
            Job existJob = ec.get();
            if (job.getSkills() != null) {
                  List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
                  List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
                  job.setSkills(dbSkills);
            }
            // private String name;
            // private String location;
            // private Double salary;
            // private Integer quantity;
            // private LevelEnum level;
            // private String description;
            // private Instant startDate;
            // private Instant endDate;
            // private boolean active;
            // private Company company;
            // private List<Skill> skills;
            existJob.setName(job.getName());
            existJob.setLocation(job.getLocation());
            existJob.setSalary(job.getSalary());
            existJob.setQuantity(job.getQuantity());
            existJob.setLevel(job.getLevel());
            existJob.setDescription(job.getDescription());
            existJob.setStartDate(job.getStartDate());
            existJob.setEndDate(job.getEndDate());
            existJob.setActive(job.isActive());
            existJob.setCompany(job.getCompany());
            existJob.setSkills(job.getSkills());

            Job newJob = this.jobRepository.save(existJob);
            return jobMapper.toResponseCreateJobDTO(newJob);
      }

      public Job fetchJobById(Long id) {
            return this.jobRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllJob(Specification<Job> specification, Pageable pageable) {

            Page<Job> pageJob = this.jobRepository.findAll(pageable);
            ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(pageJob.getTotalPages());
            meta.setTotalPages(pageJob.getTotalElements());

            resultPaginationDTO.setMeta(meta);
            resultPaginationDTO.setResult(pageJob.getContent());
            return resultPaginationDTO;
      }

}
