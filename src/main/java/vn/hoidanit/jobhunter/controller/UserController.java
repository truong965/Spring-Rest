package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
      private final UserService userService;
      private final PasswordEncoder passwordEncoder;

      public UserController(UserService userService, PasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
      }

      @PostMapping("/users")
      public ResponseEntity<User> createNewUser(@RequestBody User postmanUser) {
            postmanUser.setPassword(passwordEncoder.encode(postmanUser.getPassword()));
            User newUser = userService.saveUser(postmanUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
      }

      @DeleteMapping("/users/{id}")
      public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
      }

      @GetMapping("/users/{id}")
      public ResponseEntity<User> fetchUserById(@PathVariable("id") String id) {
            User user = userService.fetchUserById(Long.parseLong(id));
            return ResponseEntity.ok(user != null ? user : null);
      }

      @GetMapping("/users")
      @ApiMessage("fetch all users")
      public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
                  @Filter Specification<User> specification,
                  // default name (spring auto get value) page=1&size=1&sort=name,desc
                  Pageable pageable) {
            return ResponseEntity.ok(userService.fetchAllUsers(specification, pageable));
      }

      @PutMapping("/users")
      public ResponseEntity<User> updateUser(@RequestBody User user) {
            return ResponseEntity.ok(this.userService.updateUser(user));
      }
}
