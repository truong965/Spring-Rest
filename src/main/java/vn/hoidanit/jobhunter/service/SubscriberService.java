package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Service
public class SubscriberService {
      private final SubscriberRepository subscriberRepository;
      private final SkillService skillService;

      public SubscriberService(SubscriberRepository subscriberRepository, SkillService skillService) {
            this.subscriberRepository = subscriberRepository;
            this.skillService = skillService;
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
}
