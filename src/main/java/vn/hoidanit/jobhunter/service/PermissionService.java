package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@Service
public class PermissionService {
      private final PermissionRepository permissionRepository;

      public PermissionService(PermissionRepository permissionRepository) {
            this.permissionRepository = permissionRepository;
      }

      public boolean existsByModuleAndApiPathAndMethod(Permission p) {
            return permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(), p.getMethod());
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
      public Permission updatePermission(Permission permission) throws InvalidException {

            Permission exPermission = fetchPermissionById(permission.getId());
            if (exPermission != null) {

                  if (!permission.getApiPath().equals(exPermission.getApiPath())
                              || !permission.getMethod().equals(exPermission.getMethod())
                              || !permission.getModule().equals(exPermission.getModule())) {
                        if (existsByModuleAndApiPathAndMethod(permission))
                              throw new InvalidException("permission is exists");
                  }
                  exPermission.setName(permission.getName());
                  exPermission.setApiPath(permission.getApiPath());
                  exPermission.setMethod(permission.getMethod());
                  exPermission.setModule(permission.getModule());

            } else {
                  throw new InvalidException("permission is not exists");
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
