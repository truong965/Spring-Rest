package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(specification, pageable);

        ResultPaginationDTO rsDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        rsDTO.setMeta(meta);
        rsDTO.setResult(pageCompany.getContent());
        return rsDTO;
    }

    public Company fetchById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            return company.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company reqCompany) {
        Company company = this.fetchById(reqCompany.getId());
        if (company != null) {
            company.setName(reqCompany.getName());
            company.setDescription(reqCompany.getDescription());
            company.setAddress(reqCompany.getAddress());
            company.setLogo(reqCompany.getLogo());
            company = this.handleCreateCompany(company);
        }
        return company;
    }

    public void handleDeleteCompany(Long id) {
        Company company = this.fetchById(id);
        if (company != null) {
            List<User> listUser = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(listUser);
        }
        this.companyRepository.deleteById(id);
    }
}
