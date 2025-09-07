package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionByApiPathAndMethodAndModule(String apiPath, String method, String module) {
        Optional<Permission> permission = permissionRepository.findByApiPathAndMethodAndModule(apiPath, method, module);
        if (permission.isPresent()) {
            return permission.get();
        } else {
            return null;
        }
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> perOptional = this.permissionRepository.findById(id);
        if (perOptional.isPresent()) {
            return perOptional.get();
        } else {
            return null;
        }
    }

    public Permission handleUpdatePermission(Permission reqPermission) {
        Permission curPermission = this.fetchPermissionById(reqPermission.getId());
        if (curPermission != null) {
            curPermission.setApiPath(reqPermission.getApiPath());
            curPermission.setMethod(reqPermission.getMethod());
            curPermission.setModule(reqPermission.getModule());
            curPermission.setName(reqPermission.getName());
            reqPermission = this.permissionRepository.save(curPermission);
        }
        return reqPermission;
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> pageSPermission = this.permissionRepository.findAll(specification, pageable);

        ResultPaginationDTO rsDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSPermission.getTotalPages());
        meta.setTotal(pageSPermission.getTotalElements());

        rsDTO.setMeta(meta);
        rsDTO.setResult(pageSPermission.getContent());
        return rsDTO;
    }

    public void deletePermission(long id) {
        Permission permission = this.fetchPermissionById(id);
        if (permission != null) {
            permission.getRoles().forEach(p -> p.getPermissions().remove(permission));
            this.permissionRepository.delete(permission);
        }
    }

}
