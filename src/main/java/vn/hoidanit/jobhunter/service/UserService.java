package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.User;
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

      public Page<User> fetchAllUsers(Pageable pageable) {
            return userRepository.findAll(pageable);
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
}
