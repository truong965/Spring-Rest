package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        if (this.roleService.existsByName(reqRole.getName())) {
            throw new IdInvalidException("Role với name =  " + reqRole.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(reqRole));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        if (this.roleService.fetchById(reqRole.getId()) == null) {
            throw new IdInvalidException("Role với id =  " + reqRole.getId() + " không tồn tại");
        }
        // if (this.roleService.existsByName(reqRole.getName())) {
        // throw new IdInvalidException("Role với name = " + reqRole.getName() + " đã
        // tồn tại");
        // }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.update(reqRole));
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.fetchAllRoles(specification, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role by id")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role currentRole = this.roleService.fetchById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.ok().body(null);
    }

}
