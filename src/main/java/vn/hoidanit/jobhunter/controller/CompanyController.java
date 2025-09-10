package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.turkraft.springfilter.boot.Filter;

@Controller
@RequestMapping("/api/v1")
public class CompanyController {
      private final CompanyService companyService;

      public CompanyController(CompanyService companyService) {
            this.companyService = companyService;
      }

      @PostMapping("/companies")
      public ResponseEntity<Company> handleCreateCompany(@Valid @RequestBody Company company) {
            Company newCompany = this.companyService.createCompany(company);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
      }

      @PutMapping("/companies")
      public ResponseEntity<Company> handleUpdateCompany(@Valid @RequestBody Company company) {
            // TODO: process PUT request

            return ResponseEntity.ok(this.companyService.updateCompany(company));
      }

      @DeleteMapping("/companies/{id}")
      public ResponseEntity<Void> handleDelteCompany(@PathVariable Long id) {
            // TODO: process PUT request
            this.companyService.deleteCompany(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/companies")
      @ApiMessage("fetch all company")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllCompany(@Filter Specification<Company> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(companyService.fetchAllCompany(specification, pageable));
      }

      @GetMapping("/companies/{id}")
      public ResponseEntity<Company> handleFetchCompanyById(@PathVariable Long id) {
            return ResponseEntity.ok(companyService.fetchCompanyById(id));
      }

}
