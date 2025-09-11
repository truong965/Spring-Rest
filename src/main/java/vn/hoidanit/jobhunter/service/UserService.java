package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResponseCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResponseGetUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResponseUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.mapper.UserMapper;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Service
public class UserService {
      private final UserRepository userRepository;
      private final UserMapper userMapper;

      public UserService(UserRepository userRepository, UserMapper userMapper) {
            this.userRepository = userRepository;
            this.userMapper = userMapper;
      }

      @Transactional
      public ResponseCreateUserDTO saveUser(User user) throws InvalidException {
            User exUser = this.findByEmail(user.getEmail());
            if (exUser != null) {
                  throw new InvalidException("email is exits");
            }
            User newUser = userRepository.save(user);
            return userMapper.toResponseCreateUserDTO(newUser);
      }

      @Transactional
      public void deleteUser(long id) throws InvalidException {
            fetchUserById(id);
            userRepository.deleteById(id);
      }

      public ResponseGetUserDTO fetchUserById(long id) throws InvalidException {
            User exUser = userRepository.findById(id).orElse(null);
            if (exUser == null) {
                  throw new InvalidException("id= " + id + " not exists");
            }
            return userMapper.toResponseGetUserDTO(exUser);
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
            List<User> listUser = userPage.getContent();
            List<ResponseGetUserDTO> listDto = listUser.stream()
                        .map(userMapper::toResponseGetUserDTO)
                        .toList(); // Hoặc .collect(Collectors.toList());
            resultPaginationDTO.setResult(listDto);
            return resultPaginationDTO;
      }

      @Transactional
      public ResponseUpdateUserDTO updateUser(User user) throws InvalidException {
            User existingUser = this.userRepository.findById(user.getId()) != null
                        ? this.userRepository.findById(user.getId()).get()
                        : null;
            if (existingUser == null) {
                  throw new InvalidException("id= " + user.getId() + " not exists");
            }
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());
            existingUser.setAddress(user.getAddress());

            userRepository.save(existingUser);
            return userMapper.toResponseUpdateUserDTO(existingUser);
      }

      public User findByEmail(String email) {
            return this.userRepository.findByEmail(email);
      }

      @Transactional
      public void updateUserToken(String token, String email) {
            User curUser = this.findByEmail(email);
            if (curUser != null) {
                  curUser.setRefreshToken(token);
                  this.userRepository.save(curUser);
            }
      }

      public User getUserByEmailAndRefreshToken(String email, String token) {
            return userRepository.findByEmailAndRefreshToken(email, token);
      }
}
