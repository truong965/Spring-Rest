package vn.hoidanit.jobhunter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.constant.GenderEnum;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.PermissionService;

@Service
public class DatabaseInitializer implements CommandLineRunner {

      private final PermissionRepository permissionRepository;
      private final RoleRepository roleRepository;
      private final UserRepository userRepository;
      private final PasswordEncoder passwordEncoder;

      public DatabaseInitializer(
                  PermissionRepository permissionRepository,
                  RoleRepository roleRepository,
                  UserRepository userRepository,
                  PasswordEncoder passwordEncoder) {
            this.permissionRepository = permissionRepository;
            this.roleRepository = roleRepository;
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
      }

      @Override
      public void run(String... args) throws Exception {
            System.out.println("INIT DATA");
            long countPermissions = this.permissionRepository.count();
            long countRoles = this.roleRepository.count();
            long countUsers = this.userRepository.count();
            // create permissions
            if (countPermissions == 0) {
                  ArrayList<Permission> arr = new ArrayList<>();
                  // companies
                  arr.add(new Permission("create a company", "/api/v1/companies", "POST", "COMPANIES"));
                  arr.add(new Permission("update a company", "/api/v1/companies", "PUT", "COMPANIES"));
                  arr.add(new Permission("delete a company", "/api/v1/companies/{id}", "DELETE", "COMPANIES"));
                  arr.add(new Permission("fetch a company by id", "/api/v1/companies/{id}", "GET", "COMPANIES"));
                  arr.add(new Permission("fetch all company with pagination", "/api/v1/companies", "GET", "COMPANIES"));
                  // jobs
                  arr.add(new Permission("create a job", "/api/v1/jobs", "POST", "JOBS"));
                  arr.add(new Permission("update a job", "/api/v1/jobs", "PUT", "JOBS"));
                  arr.add(new Permission("delete a job", "/api/v1/jobs/{id}", "DELETE", "JOBS"));
                  arr.add(new Permission("fetch a job by id", "/api/v1/jobs/{id}", "GET", "JOBS"));
                  arr.add(new Permission("fetch all jobs with pagination", "/api/v1/jobs", "GET", "JOBS"));
                  // permissions
                  arr.add(new Permission("create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
                  arr.add(new Permission("update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
                  arr.add(new Permission("delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
                  arr.add(new Permission("fetch a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
                  arr.add(new Permission("fetch all permissions with pagination", "/api/v1/permissions", "GET",
                              "PERMISSIONS"));
                  // resumes
                  arr.add(new Permission("create a resume", "/api/v1/resumes", "POST", "RESUMES"));
                  arr.add(new Permission("update a resume", "/api/v1/resumes", "PUT", "RESUMES"));
                  arr.add(new Permission("delete a resume", "/api/v1/resumes/{id}", "DELETE", "RESUMES"));
                  arr.add(new Permission("fetch a resume by id", "/api/v1/resumes/{id}", "GET", "RESUMES"));
                  arr.add(new Permission("fetch all resumes with pagination", "/api/v1/resumes", "GET", "RESUMES"));
                  // roles
                  arr.add(new Permission("create a role", "/api/v1/roles", "POST", "ROLES"));
                  arr.add(new Permission("update a role", "/api/v1/roles", "PUT", "ROLES"));
                  arr.add(new Permission("delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
                  arr.add(new Permission("fetch a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
                  arr.add(new Permission("fetch all roles with pagination", "/api/v1/roles", "GET", "ROLES"));
                  // users
                  arr.add(new Permission("create a user", "/api/v1/users", "POST", "USERS"));
                  arr.add(new Permission("update a user", "/api/v1/users", "PUT", "USERS"));
                  arr.add(new Permission("delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
                  arr.add(new Permission("fetch a user by id", "/api/v1/users/{id}", "GET", "USERS"));
                  arr.add(new Permission("fetch all users with pagination", "/api/v1/users", "GET", "USERS"));
                  // subscriber
                  arr.add(new Permission("create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
                  arr.add(new Permission("update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
                  arr.add(new Permission("delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
                  arr.add(new Permission("fetch a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
                  arr.add(new Permission("fetch all subscriber with pagination", "/api/v1/subscribers", "GET",
                              "SUBSCRIBERS"));

                  // file
                  arr.add(new Permission("download a file", "/api/v1/files", "POST", "FILES"));

                  // email
                  arr.add(new Permission("send email", "/api/v1/email", "GET", "EMAILS"));
                  this.permissionRepository.saveAll(arr);
            }
            if (countRoles == 0) {
                  List<Permission> allPermissions = this.permissionRepository.findAll();
                  // create admin
                  Role adminRole = new Role();
                  adminRole.setName("SUPER_ADMIN");
                  adminRole.setDescription("admin with full permissions");
                  adminRole.setActive(true);
                  adminRole.setPermissions(allPermissions);
                  this.roleRepository.save(adminRole);
            }
            if (countUsers == 0) {
                  User adminUser = new User();
                  adminUser.setName("admin");
                  adminUser.setEmail("admin@gmail.com");
                  adminUser.setAge(25);
                  adminUser.setGender(GenderEnum.MALE);
                  adminUser.setPassword(this.passwordEncoder.encode("123456"));

                  Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
                  adminUser.setRole(adminRole);
                  this.userRepository.save(adminUser);
            }
            if (countPermissions == 0 || countRoles == 0 || countUsers == 0) {
                  System.out.println("INIT DATA SUCCESS");
            } else {
                  System.out.println("DATA EXISTED, SKIP INIT");
            }
      }
}
