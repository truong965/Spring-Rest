package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
      private final UserService userService;
      private static final int PAGE_SIZE = 5;

      public UserController(UserService userService) {
            this.userService = userService;
      }

      @PostMapping("/users")
      public ResponseEntity<User> createNewUser(@RequestBody User postNameUser) {
            User newUser = userService.saveUser(postNameUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
      }

      @DeleteMapping("/users/{id}")
      public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
            if (id > 1000) {
                  throw new IdInvalidException("ID is invalid, must be less than 1000");
            }
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
      }

      @GetMapping("/users/{id}")
      public ResponseEntity<User> fetchUserById(@PathVariable("id") String id) {
            User user = userService.fetchUserById(Long.parseLong(id));
            return ResponseEntity.ok(user != null ? user : null);
      }

      @GetMapping("/users")
      public ResponseEntity<List<User>> fetchAllUsers() {
            Pageable pageable = PageRequest.of(0, PAGE_SIZE);
            Page<User> users = userService.fetchAllUsers(pageable);
            //
            return ResponseEntity.ok(users.getContent());
      }

      @PutMapping("/users")
      public ResponseEntity<User> updateUser(@RequestBody User user) {
            return ResponseEntity.ok(this.userService.updateUser(user));
      }
}
