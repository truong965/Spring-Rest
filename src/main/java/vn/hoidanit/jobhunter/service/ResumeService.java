package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseUpdatedResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.mapper.ResumeMapper;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Service
public class ResumeService {

      private final ResumeRepository resumeRepository;
      private final JobService jobService;
      private final UserService userService;
      private final ResumeMapper resumeMapper;

      @Autowired
      private FilterParser filterParser;

      @Autowired
      private FilterSpecificationConverter filterSpecificationConverter;

      public ResumeService(ResumeRepository resumeRepository, ResumeMapper resumeMapper, JobService jobService,
                  UserService userService) {
            this.resumeRepository = resumeRepository;
            this.resumeMapper = resumeMapper;
            this.jobService = jobService;
            this.userService = userService;
      }

      @Transactional
      public ResponseCreateResumeDTO createResume(Resume resume) throws InvalidException {
            // check job
            if (resume.getJob() != null) {
                  Job exJob = jobService.fetchJobById(resume.getJob().getId());
                  resume.setJob(exJob);
            }
            if (resume.getUser() != null) {
                  User exUser = userService.findById(resume.getUser().getId());
                  resume.setUser(exUser);
            }
            Resume newResume = this.resumeRepository.save(resume);
            return resumeMapper.toResponseCreateResumeDTO(newResume);
      }

      @Transactional
      public void deleteResume(Long id) {
            this.resumeRepository.deleteById(id);
      }

      @Transactional
      public ResponseUpdatedResumeDTO updateResume(Resume resume) {
            Optional<Resume> ec = this.resumeRepository.findById(resume.getId());
            if (!ec.isPresent())
                  return null;
            Resume existResume = ec.get();
            existResume.setStatus(resume.getStatus());

            Resume newResume = this.resumeRepository.save(existResume);
            ResponseUpdatedResumeDTO responseUpdatedResumeDTO = new ResponseUpdatedResumeDTO(newResume.getUpdatedAt(),
                        newResume.getUpdatedBy());
            return responseUpdatedResumeDTO;
      }

      public ResponseGetResumeDTO fetchResumeById(Long id) {
            Resume resume = this.resumeRepository.findById(id).orElse(null);
            if (resume.getJob() != null) {
                  return resumeMapper.toResponseGetResumeDTO(resume, resume.getJob().getCompany().getName());
            }
            return resumeMapper.toResponseGetResumeDTO(resume);
      }

      // Hàm helper riêng
      private ResultPaginationDTO createPaginationResult(Page<Resume> pageResume, Pageable pageable) {
            ResultPaginationDTO dto = new ResultPaginationDTO();
            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(pageResume.getTotalPages());
            meta.setTotalPages(pageResume.getTotalElements());
            dto.setMeta(meta);

            List<ResponseGetResumeDTO> listDto = pageResume.getContent().stream()
                        .map(resumeMapper::toResponseGetResumeDTO)
                        .toList();
            dto.setResult(listDto);
            return dto;
      }

      // Các hàm của bạn giờ sẽ rất gọn gàng
      public ResultPaginationDTO fetchAllResume(Specification<Resume> specification, Pageable pageable) {
            Page<Resume> pageResume = this.resumeRepository.findAll(specification, pageable); // Đã sửa lỗi
            return createPaginationResult(pageResume, pageable);
      }

      public ResultPaginationDTO fetchAllResumeByUser(Pageable pageable) {
            // Lấy email của người dùng đang đăng nhập từ SecurityUtil
            String email = SecurityUtil.getCurrentUserLogin().get(); // Giả sử user luôn tồn tại
            // Tự xây dựng một chuỗi điều kiện lọc: ("email='" + email + "'")
            FilterNode node = filterParser.parse("email='" + email + "'");
            // Sử dụng filterParser và filterSpecificationConverter để chuyển chuỗi này
            // thành một đối tượng Specification
            FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
            // Lấy resume của duy nhất người dùng đang đăng nhập, client không thể lọc.
            Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
            return createPaginationResult(pageResume, pageable);
      }
}
