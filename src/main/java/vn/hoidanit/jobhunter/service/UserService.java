package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO.ResUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Company cOptional = this.companyService.fetchById(user.getCompany().getId());
            user.setCompany(cOptional);
        }
        // check role
        if (user.getRole() != null) {
            Role rOptional = this.roleService.fetchById(user.getRole().getId());
            user.setRole(rOptional);
        }

        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO rsDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rsDTO.setMeta(meta);

        List<ResUserDTO> userList = new ArrayList<>();
        for (User user : pageUser.getContent()) {
            ResUserDTO userDTO = new ResUserDTO();
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
                ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
                companyUser.setId(user.getCompany().getId());
                companyUser.setName(user.getCompany().getName());
                userDTO.setCompany(companyUser);
            }
            if (user.getRole() != null) {
                ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
                roleUser.setId(user.getRole().getId());
                roleUser.setName(user.getRole().getName());
                userDTO.setRole(roleUser);
            }
            userList.add(userDTO);
        }
        rsDTO.setResult(userList);

        // remove sensitive data
        // List<ResUserDTO> listUser = pageUser.getContent()
        // .stream().map(item -> new ResUserDTO(
        // item.getId(),
        // item.getEmail(),
        // item.getName(),
        // item.getGender(),
        // item.getAddress(),
        // item.getAge(),
        // item.getUpdatedAt(),
        // item.getCreatedAt()))
        // .collect(Collectors.toList());

        // rs.setResult(listUser);

        return rsDTO;
    }

    public User handleUpdateUser(User requestUser) {
        User currentUser = this.fetchUserById(requestUser.getId());
        if (currentUser != null) {
            currentUser.setGender(requestUser.getGender());
            currentUser.setName(requestUser.getName());
            currentUser.setAge(requestUser.getAge());
            currentUser.setAddress(requestUser.getAddress());

            // check company
            if (requestUser.getCompany() != null) {
                Company cOptional = this.companyService.fetchById(requestUser.getCompany().getId());
                currentUser.setCompany(cOptional);
            }
            // check role
            if (requestUser.getRole() != null) {
                Role rOptional = this.roleService.fetchById(requestUser.getRole().getId());
                currentUser.setRole(rOptional);
            }

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean isValid(String email) {
        return this.userRepository.existsByEmail(email); // Kiểm tra email có tồn tại không
    }

    public void handleSaveToken(String token, String email) {
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User fetchUserByRefreshTokenAndEmail(String email, String token) {
        return this.userRepository.findByEmailAndRefreshToken(email, token);
    }

    public List<User> fetchByCompany(Company company) {
        return this.fetchByCompany(company);
    }

    public void handleDeleteAllUser(List<User> users) {
        this.userRepository.deleteAll(users);
    }
}
