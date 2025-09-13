package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
      private final RoleRepository roleRepository;
      private final PermissionRepository permissionRepository;

      public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
            this.roleRepository = roleRepository;
            this.permissionRepository = permissionRepository;
      }

      public boolean existsByName(String name) {
            return this.roleRepository.existsByName(name);
      }

      @Transactional
      public Role createRole(Role role) {
            if (role.getPermissions() != null) {
                  List<Long> listPermissionsId = role.getPermissions().stream().map(Permission::getId).toList();
                  List<Permission> listPermissions = permissionRepository.findByIdIn(listPermissionsId);
                  role.setPermissions(listPermissions);
            }
            return this.roleRepository.save(role);
      }

      @Transactional
      public void deleteRole(Long id) {
            this.roleRepository.deleteById(id);
      }

      @Transactional
      public Role updateRole(Role role) {
            Role exRole = fetchRoleById(role.getId());

            if (exRole != null) {
                  if (role.getPermissions() != null) {
                        List<Long> listPermissionsId = role.getPermissions().stream().map(Permission::getId).toList();
                        List<Permission> listPermissions = permissionRepository.findByIdIn(listPermissionsId);
                        exRole.setPermissions(listPermissions);
                  }
                  exRole.setName(role.getName());
                  exRole.setDescription(role.getDescription());
                  exRole.setActive(role.isActive());
                  return this.roleRepository.save(exRole);

            }
            return null;
      }

      public Role fetchRoleById(Long id) {
            return this.roleRepository.findById(id).orElse(null);
      }

      public ResultPaginationDTO fetchAllRole(Specification<Role> specification, Pageable pageable) {

            Page<Role> pageJob = this.roleRepository.findAll(specification, pageable);
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
