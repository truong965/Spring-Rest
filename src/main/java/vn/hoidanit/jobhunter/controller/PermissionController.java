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
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
      private final PermissionService permissionService;

      public PermissionController(PermissionService permissionService) {
            this.permissionService = permissionService;
      }

      @PostMapping("/permissions")
      @ApiMessage("create permissions")
      public ResponseEntity<Permission> handleCreatePermission(@Valid @RequestBody Permission permission)
                  throws InvalidException {
            if (permissionService.existsByModuleAndApiPathAndMethod(permission)) {
                  throw new InvalidException("permission is exists");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
      }

      @PutMapping("/permissions")
      @ApiMessage("update permissions")
      public ResponseEntity<Permission> handleUpdatePermission(@Valid @RequestBody Permission permission)
                  throws InvalidException {
            return ResponseEntity.ok(this.permissionService.updatePermission(permission));
      }

      @DeleteMapping("/permissions/{id}")
      @ApiMessage("delete permissions")
      public ResponseEntity<Void> handleDeletePermission(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            if (permissionService.fetchPermissionById(id) == null) {
                  throw new InvalidException("permission is not exists");
            }
            this.permissionService.deletePermission(id);
            return ResponseEntity.ok(null);
      }

      @GetMapping("/permissions")
      @ApiMessage("fetch all permissions")
      public ResponseEntity<ResultPaginationDTO> handleFetchAllJob(@Filter Specification<Permission> specification,
                  Pageable pageable) {
            return ResponseEntity.ok(permissionService.fetchAllPermission(specification, pageable));
      }

      @GetMapping("/permissions/{id}")
      @ApiMessage("fetch permissions by id")
      public ResponseEntity<Permission> handleFetchPermissionById(@PathVariable("id") Long id) throws InvalidException {
            // TODO: process PUT request
            if (permissionService.fetchPermissionById(id) == null) {
                  throw new InvalidException("permission is not exists");
            }
            return ResponseEntity.ok(permissionService.fetchPermissionById(id));
      }
}
