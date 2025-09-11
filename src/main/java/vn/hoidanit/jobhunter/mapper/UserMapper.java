package vn.hoidanit.jobhunter.mapper;

import org.mapstruct.Mapper;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResponseCompanyUser;
import vn.hoidanit.jobhunter.domain.response.ResponseCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseGetUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseUpdateUserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
      // UserMapper INSTANT = Mappers.getMapper(UserMapper.class);

      ResponseCreateUserDTO toResponseCreateUserDTO(User user);

      ResponseGetUserDTO toResponseGetUserDTO(User user);

      ResponseUpdateUserDTO toResponseUpdateUserDTO(User user);

      // Phương thức phụ để xử lý mapping Company
      // MapStruct sẽ tự động dùng phương thức này khi cần chuyển Company ->
      // ResponseCompany
      default ResponseCompanyUser companyToResponseCompany(Company company) {
            if (company == null) {
                  return null;
            }
            ResponseCompanyUser responseCompany = new ResponseCompanyUser();
            responseCompany.setId(company.getId());
            responseCompany.setName(company.getName());
            return responseCompany;
      }
}