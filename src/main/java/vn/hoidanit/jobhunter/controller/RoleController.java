package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
      private final RoleService roleService;

      public RoleController(RoleService roleService) {
            this.roleService = roleService;
      }

      @PostMapping("/roles")
      @ApiMessage("create roles")
      public ResponseEntity<Role> handleCreateRole(@Valid @RequestBody Role role) throws InvalidException {
            if (this.roleService.existsByName(role.getName())) {
                  throw new InvalidException("role is exists");
            }
            Role newRole = this.roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
      }

      @PutMapping("/roles")
      @ApiMessage("update roles")
      public ResponseEntity<Role> handleUpdateRole(@Valid @RequestBody Role role)
                  throws InvalidException {
            // TODO: process PUT request
            Role currentRole = this.roleService.fetchRoleById(role.getId());
            if (currentRole == null) {
                  throw new InvalidException("roles id =" + role.getId() + " is not exists");
            }
            return ResponseEntity.ok(this.roleService.updateRole(role));
      }

      @DeleteMapping("/roles/{id}")
      @ApiMessage("delete roles")
      public ResponseEntity<Void> handleDeleteRole(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            Role currentRole = this.roleService.fetchRoleById(id);
            if (currentRole == null) {
                  throw new InvalidException("roles id =" + id + " is not exists");
            }
            this.roleService.deleteRole(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/roles")
      @ApiMessage("fetch all roles")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllroles(@Filter Specification<Role> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(roleService.fetchAllRole(specification, pageable));
      }

      @GetMapping("/roles/{id}")
      @ApiMessage("fetch roles by id")
      public ResponseEntity<Role> handleFetchrolesById(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            Role currentRole = this.roleService.fetchRoleById(id);
            if (currentRole == null) {
                  throw new InvalidException("roles id =" + id + " is not exists");
            }
            return ResponseEntity.ok(roleService.fetchRoleById(id));
      }

}
