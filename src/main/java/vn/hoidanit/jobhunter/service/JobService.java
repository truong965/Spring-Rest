package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.mapper.JobMapper;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
      private final JobRepository jobRepository;
      private final SkillRepository skillRepository;
      private final CompanyRepository companyRepository;
      private final JobMapper jobMapper;

      public JobService(JobRepository jobRepository, SkillRepository skillRepository, JobMapper jobMapper,
                  CompanyRepository companyRepository) {
            this.jobRepository = jobRepository;
            this.skillRepository = skillRepository;
            this.jobMapper = jobMapper;
            this.companyRepository = companyRepository;
      }

      @Transactional
      public ResponseCreateJobDTO createJob(Job job) {
            // check skill
            if (job.getSkills() != null) {
                  List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
                  List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
                  job.setSkills(dbSkills);
            }
            if (job.getCompany() != null) {
                  Company company = this.companyRepository.findById(job.getCompany().getId()).orElse(null);
                  if (company != null) {
                        job.setCompany(company);
                  }
            }
            Job newJob = this.jobRepository.save(job);
            return jobMapper.toResponseCreateJobDTO(newJob);
      }

      @Transactional
      public void deleteJob(Long id) {
            this.jobRepository.deleteById(id);
      }

      @Transactional
      public ResponseCreateJobDTO updateJob(Job job, Job jobInDB) {
            if (job.getSkills() != null) {
                  List<Long> reqSkills = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
                  List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
                  jobInDB.setSkills(dbSkills);
            }
            if (job.getCompany() != null) {
                  Company company = this.companyRepository.findById(job.getCompany().getId()).orElse(null);
                  if (company != null) {
                        jobInDB.setCompany(company);
                  }
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
            jobInDB.setName(job.getName());
            jobInDB.setLocation(job.getLocation());
            jobInDB.setSalary(job.getSalary());
            jobInDB.setQuantity(job.getQuantity());
            jobInDB.setLevel(job.getLevel());
            jobInDB.setDescription(job.getDescription());
            jobInDB.setStartDate(job.getStartDate());
            jobInDB.setEndDate(job.getEndDate());
            jobInDB.setActive(job.isActive());

            Job newJob = this.jobRepository.save(jobInDB);
            return jobMapper.toResponseCreateJobDTO(newJob);
      }

      public Job fetchJobById(Long id) {
            return this.jobRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllJob(Specification<Job> specification, Pageable pageable) {

            Page<Job> pageJob = this.jobRepository.findAll(specification, pageable);
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
