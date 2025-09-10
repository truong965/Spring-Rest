package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

      private final UserRepository userRepository;
      private final CompanyRepository companyRepository;

      public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
            this.companyRepository = companyRepository;
            this.userRepository = userRepository;
      }

      @Transactional
      public Company createCompany(Company company) {
            return this.companyRepository.save(company);
      }

      @Transactional
      public void deleteCompany(Long id) {
            this.companyRepository.deleteById(id);
      }

      @Transactional
      public Company updateCompany(Company company) {
            Optional<Company> ec = this.companyRepository.findById(company.getId());
            if (!ec.isPresent())
                  return null;
            Company existCompany = ec.get();

            existCompany.setName(company.getName());
            existCompany.setAddress(company.getAddress());
            existCompany.setDescription(company.getDescription());
            existCompany.setLogo(company.getLogo());
            return this.companyRepository.save(existCompany);
      }

      public Company fetchCompanyById(Long id) {
            return this.companyRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllCompany(Specification<Company> specification, Pageable pageable) {

            Page<Company> pageCompany = this.companyRepository.findAll(pageable);
            ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

            Meta meta = new Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(pageCompany.getTotalPages());
            meta.setTotalPages(pageCompany.getTotalElements());

            resultPaginationDTO.setMeta(meta);
            resultPaginationDTO.setResult(pageCompany.getContent());
            return resultPaginationDTO;
      }
}
