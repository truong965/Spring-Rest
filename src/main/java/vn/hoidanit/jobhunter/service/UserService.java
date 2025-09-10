package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
      private final UserRepository userRepository;

      public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
      }

      @Transactional
      public User saveUser(User user) {

            return userRepository.save(user);
      }

      @Transactional
      public void deleteUser(long id) {
            userRepository.deleteById(id);
      }

      public User fetchUserById(long id) {
            return userRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllUsers(Specification<User> specification, Pageable pageable) {
            Page<User> userPage = userRepository.findAll(pageable);
            ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

            Meta meta = new Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(userPage.getTotalPages());
            meta.setTotalPages(userPage.getTotalElements());

            resultPaginationDTO.setMeta(meta);
            resultPaginationDTO.setResult(userPage.getContent());
            return resultPaginationDTO;
      }

      @Transactional
      public User updateUser(User user) {
            User existingUser = this.fetchUserById(user.getId());
            if (existingUser == null) {
                  return null;
            }
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            return userRepository.save(existingUser);
      }

      public User findByEmail(String email) {
            return this.userRepository.findByEmail(email);
      }
}
