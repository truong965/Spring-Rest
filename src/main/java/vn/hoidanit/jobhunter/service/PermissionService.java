package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class PermissionService {
      private final PermissionRepository permissionRepository;
      private final RoleRepository roleRepository;

      public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
            this.permissionRepository = permissionRepository;
            this.roleRepository = roleRepository;
      }

      public boolean existsByModuleAndApiPathAndMethod(Permission p) {
            return permissionRepository.existsByModuleAndApiPathAndMethod(p);
      }

      @Transactional
      public Permission createPermission(Permission permission) {
            Permission newPermission = this.permissionRepository.save(permission);
            return newPermission;
      }

      @Transactional
      public void deletePermission(Long id) {
            Permission exPermission = fetchPermissionById(id);
            if (exPermission != null) {
                  // remove form permisson_role
                  exPermission.getRoles().forEach(r -> r.getPermissions().remove(exPermission));
                  this.permissionRepository.delete(exPermission);
            }
      }

      @Transactional
      public Permission updatePermission(Permission permission) {
            Permission exPermission = fetchPermissionById(permission.getId());
            if (exPermission != null) {
                  exPermission.setName(permission.getName());
                  exPermission.setApiPath(permission.getApiPath());
                  exPermission.setMethod(permission.getMethod());
                  exPermission.setModule(permission.getModule());
            }
            Permission newPermission = this.permissionRepository.save(exPermission);
            return newPermission;
      }

      public Permission fetchPermissionById(Long id) {
            return this.permissionRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllPermission(Specification<Permission> specification, Pageable pageable) {

            Page<Permission> pageJob = this.permissionRepository.findAll(specification, pageable);
            ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setPages(pageJob.getTotalPages());
            meta.setTotalPages(pageJob.getTotalElements());

            resultPaginationDTO.setMeta(meta);
            resultPaginationDTO.setResult(pageJob.getContent());
            return resultPaginationDTO;
      }
}
