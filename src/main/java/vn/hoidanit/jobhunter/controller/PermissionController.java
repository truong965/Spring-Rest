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
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission reqPermission) throws IdInvalidException {
        if (this.permissionService.fetchPermissionByApiPathAndMethodAndModule(reqPermission.getApiPath(),
                reqPermission.getMethod(), reqPermission.getModule()) != null) {
            throw new IdInvalidException("permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleCreatePermission(reqPermission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission reqPermission) throws IdInvalidException {
        if (this.permissionService.fetchPermissionById(reqPermission.getId()) == null) {
            throw new IdInvalidException("permission với id = " + reqPermission.getId() + " không tồn tại");
        }
        if (this.permissionService.fetchPermissionByApiPathAndMethodAndModule(reqPermission.getApiPath(),
                reqPermission.getMethod(), reqPermission.getModule()) != null) {
            throw new IdInvalidException("permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleUpdatePermission(reqPermission));
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(
            @Filter Specification<Permission> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.fetchAllPermissions(specification, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission currentPermission = this.permissionService.fetchPermissionById(id);
        if (currentPermission == null) {
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }
}
