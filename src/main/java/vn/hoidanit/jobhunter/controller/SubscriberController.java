package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
      private final SubscriberService subscriberService;

      public SubscriberController(SubscriberService subscriberService) {
            this.subscriberService = subscriberService;
      }

      // create( check email exists , check skilss exists)
      @PostMapping("/subscribers")
      @ApiMessage("create subscribers")
      public ResponseEntity<Subscriber> handleCreateSubscriber(@Valid @RequestBody Subscriber subscriber)
                  throws InvalidException {
            if (subscriberService.isEmailExists(subscriber.getEmail())) {
                  throw new InvalidException("subscribers email =" + subscriber.getEmail() + " is exists");
            }
            Subscriber newSubscriber = this.subscriberService.createSubscriber(subscriber);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);
      }

      // @PostMapping("/subscribers")
      // update subscribers ( check skilss exists)
      @PutMapping("/subscribers")
      @ApiMessage("update subscribers")
      public ResponseEntity<Subscriber> handleUpdateSubscriber(@Valid @RequestBody Subscriber subscriber)
                  throws InvalidException {
            Subscriber currentSubscriber = this.subscriberService.fetchSubscriberById(subscriber.getId());
            if (currentSubscriber == null) {
                  throw new InvalidException("subscribers id =" + subscriber.getId() + " is not exists");
            }
            return ResponseEntity.ok(this.subscriberService.updateSubscriber(subscriber, currentSubscriber));
      }

      @PostMapping("/subscribers/skills")
      @ApiMessage("get subscribers's skills")
      public ResponseEntity<Subscriber> getSubcriberSkill() {
            String email = SecurityUtil.getCurrentUserLogin().orElse(null);
            return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
      }

}
