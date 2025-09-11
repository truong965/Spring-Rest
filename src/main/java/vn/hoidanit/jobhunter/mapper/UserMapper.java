package vn.hoidanit.jobhunter.mapper;

import org.mapstruct.Mapper;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResponseCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResponseGetUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResponseUpdateUserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
      // UserMapper INSTANT = Mappers.getMapper(UserMapper.class);

      ResponseCreateUserDTO toResponseCreateUserDTO(User user);

      ResponseGetUserDTO toResponseGetUserDTO(User user);

      ResponseUpdateUserDTO toResponseUpdateUserDTO(User user);
}