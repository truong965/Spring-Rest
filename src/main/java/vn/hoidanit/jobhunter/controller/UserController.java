package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO.ResCreatedUserDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO.ResUpdatedUserDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO.ResUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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
    @ApiMessage("create user")
    public ResponseEntity<ResCreatedUserDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {
        if (this.userService.isValid(user.getEmail())) {
            throw new IdInvalidException("Tài khoản email đã tồn tại");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userCreated = this.userService.handleCreateUser(user);

        ResCreatedUserDTO cUserDTO = new ResCreatedUserDTO();
        ResCreatedUserDTO.CompanyUser companyUser = new ResCreatedUserDTO.CompanyUser();
        cUserDTO.setId(userCreated.getId());
        cUserDTO.setAddress(userCreated.getAddress());
        cUserDTO.setAge(userCreated.getAge());
        cUserDTO.setCreatedAt(userCreated.getCreatedAt());
        cUserDTO.setCreatedBy(userCreated.getCreatedBy());
        cUserDTO.setEmail(userCreated.getEmail());
        cUserDTO.setGender(userCreated.getGender());
        cUserDTO.setName(userCreated.getName());
        if (userCreated.getCompany() != null) {
            companyUser.setId(userCreated.getCompany().getId());
            companyUser.setName(userCreated.getCompany().getName());
            cUserDTO.setCompany(companyUser);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cUserDTO); // always need body
    }

    // @ExceptionHandler(value = IdInvalidException.class)
    // public ResponseEntity<String> handleIdException(IdInvalidException
    // idException) {
    // return ResponseEntity.badRequest().body(idException.getMessage());
    // }
    // only used at UserController -> local => go GlobalException to define global
    // excep to use at all controller

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (this.userService.fetchUserById(id) == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        // return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        if (this.userService.fetchUserById(id) == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        User user = this.userService.fetchUserById(id);

        ResUserDTO userDTO = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();

        userDTO.setId(user.getId());
        userDTO.setAddress(user.getAddress());
        userDTO.setAge(user.getAge());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setUpdatedBy(user.getUpdatedBy());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setName(user.getName());
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            userDTO.setCompany(companyUser);
        }

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> specification,
            Pageable pageable) {

        return ResponseEntity.ok(this.userService.fetchAllUser(specification, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update user")
    public ResponseEntity<ResUpdatedUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        if (this.userService.fetchUserById(user.getId()) == null) {
            throw new IdInvalidException("Người dùng không tồn tại");
        }
        User userCreated = this.userService.handleUpdateUser(user);

        ResUpdatedUserDTO cUserDTO = new ResUpdatedUserDTO();
        ResUpdatedUserDTO.CompanyUser companyUser = new ResUpdatedUserDTO.CompanyUser();

        cUserDTO.setId(userCreated.getId());
        cUserDTO.setAddress(userCreated.getAddress());
        cUserDTO.setAge(userCreated.getAge());
        cUserDTO.setUpdatedAt(userCreated.getUpdatedAt());
        cUserDTO.setUpdatedBy(userCreated.getUpdatedBy());
        cUserDTO.setEmail(userCreated.getEmail());
        cUserDTO.setGender(userCreated.getGender());
        cUserDTO.setName(userCreated.getName());
        if (userCreated.getCompany() != null) {
            companyUser.setId(userCreated.getCompany().getId());
            companyUser.setName(userCreated.getCompany().getName());
            cUserDTO.setCompany(companyUser);
        }

        return ResponseEntity.ok(cUserDTO);
    }
}
