package vn.hoidanit.jobhunter.mapper;

import org.mapstruct.Mapper;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseResumeJob;
import vn.hoidanit.jobhunter.domain.response.ResponseResumeUser;
import vn.hoidanit.jobhunter.domain.response.ResponseUpdatedResumeDTO;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
      ResponseCreateResumeDTO toResponseCreateResumeDTO(Resume resume);

      ResponseGetResumeDTO toResponseGetResumeDTO(Resume resume);

      ResponseGetResumeDTO toResponseGetResumeDTO(Resume resume, String companyName);

      default ResponseResumeUser userToResponseResumeUser(User user) {
            if (user == null) {
                  return null;
            }
            ResponseResumeUser rep = new ResponseResumeUser();
            rep.setId(user.getId());
            rep.setName(user.getName());
            return rep;
      }

      default ResponseResumeJob jobToResponseResumeJob(Job job) {
            if (job == null) {
                  return null;
            }
            ResponseResumeJob rep = new ResponseResumeJob();
            rep.setId(job.getId());
            rep.setName(job.getName());
            return rep;
      }
}
