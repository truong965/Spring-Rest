package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

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
      @ApiMessage("create new user")
      public ResponseEntity<ResponseCreateUserDTO> createNewUser(@Valid @RequestBody User postmanUser)
                  throws InvalidException {
            postmanUser.setPassword(passwordEncoder.encode(postmanUser.getPassword()));
            ResponseCreateUserDTO userResponseDTO = userService.saveUser(postmanUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
      }

      @DeleteMapping("/users/{id}")
      @ApiMessage("delete a user")
      public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws InvalidException {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
      }

      @GetMapping("/users/{id}")
      public ResponseEntity<ResponseGetUserDTO> fetchUserById(@PathVariable("id") String id) throws InvalidException {
            ResponseGetUserDTO user = userService.fetchUserById(Long.parseLong(id));
            return ResponseEntity.ok(user != null ? user : null);
      }

      @GetMapping("/users")
      @ApiMessage("fetch all users")
      public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
                  @Filter Specification<User> specification,
                  // default name (spring auto get value) page=1&size=1&sort=name,desc
                  Pageable pageable) throws InvalidException {
            return ResponseEntity.ok(userService.fetchAllUsers(specification, pageable));
      }

      @PutMapping("/users")
      @ApiMessage("update user")
      public ResponseEntity<ResponseUpdateUserDTO> updateUser(@Valid @RequestBody User user) throws InvalidException {
            return ResponseEntity.ok(this.userService.updateUser(user));
      }
}
