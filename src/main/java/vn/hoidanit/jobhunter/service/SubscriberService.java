package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.ResposnseEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Service
public class SubscriberService {
      private final SubscriberRepository subscriberRepository;
      private final SkillService skillService;
      private final JobRepository jobRepository;
      private final EmailService emailService;

      public SubscriberService(SubscriberRepository subscriberRepository, SkillService skillService,
                  JobRepository jobRepository, EmailService emailService) {
            this.subscriberRepository = subscriberRepository;
            this.skillService = skillService;
            this.jobRepository = jobRepository;
            this.emailService = emailService;
      }

      public boolean isEmailExists(String email) {
            return subscriberRepository.existsByEmail(email);
      }

      @Transactional
      // create subscriber ( check skills exists)
      public Subscriber createSubscriber(Subscriber subscriber) throws InvalidException {
            List<Skill> skills = subscriber.getSkills();
            if (skills != null) {
                  List<Skill> existingSkills = skillService
                              .fetchSkillsByIds(skills.stream().map(Skill::getId).toList());
                  if (existingSkills.size() == 0) {
                        throw new InvalidException("No valid skills found for the subscriber.");
                  }
                  subscriber.setSkills(existingSkills);
            }

            return subscriberRepository.save(subscriber);
      }

      public Subscriber fetchSubscriberById(Long id) {
            return this.subscriberRepository.findById(id).orElse(null);
      }

      @Transactional
      // update subscribers ( check skilss exists)
      public Subscriber updateSubscriber(Subscriber subscriber, Subscriber exSubscriber) throws InvalidException {
            List<Skill> skills = subscriber.getSkills();
            if (skills != null) {
                  List<Skill> existingSkills = skillService
                              .fetchSkillsByIds(skills.stream().map(Skill::getId).toList());
                  if (existingSkills.size() == 0) {
                        throw new InvalidException("No valid skills found for the subscriber.");
                  }
                  exSubscriber.setSkills(existingSkills);
            }
            return this.subscriberRepository.save(exSubscriber);
      }

      public void sendSubscribersEmailJobs() {
            // phân trang thay vi lay tất cả
            List<Subscriber> listSubs = this.subscriberRepository.findAll();
            if (listSubs != null && listSubs.size() > 0) {
                  for (Subscriber sub : listSubs) {
                        List<Skill> listSkills = sub.getSkills();
                        if (listSkills != null && listSkills.size() > 0) {
                              // lấy job theo skill
                              List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                              if (listJobs != null && listJobs.size() > 0) {
                                    // convert job to ResposnseEmailJob
                                    List<ResposnseEmailJob> arr = listJobs.stream()
                                                .map(job -> this.convertJobToSendEmail(job))
                                                .collect(Collectors.toList());

                                    this.emailService.sendEmailFromTemplateSync(
                                                sub.getEmail(),
                                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                                "job",
                                                sub.getName(),
                                                arr);
                              }
                        }
                  }
            }
      }

      public ResposnseEmailJob convertJobToSendEmail(Job job) {
            ResposnseEmailJob res = new ResposnseEmailJob();
            res.setName(job.getName());
            res.setSalary(job.getSalary());
            res.setCompany(new ResposnseEmailJob.CompanyEmail(job.getCompany().getName()));
            List<Skill> skills = job.getSkills();
            List<ResposnseEmailJob.SkillEmail> s = skills.stream()
                        .map(skill -> new ResposnseEmailJob.SkillEmail(skill.getName()))
                        .collect(Collectors.toList());
            res.setSkills(s);
            return res;
      }

      public Subscriber findByEmail(String email) {
            return this.subscriberRepository.findByEmail(email);
      }
      // cron job
      // @Scheduled
      // public void
}
