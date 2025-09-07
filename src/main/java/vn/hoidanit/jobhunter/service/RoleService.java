package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.Pagination.ResultPaginationDTO;
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

    public Role create(Role role) {
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> listPermissions = this.permissionRepository.findByIdIn(reqPermissions);

            role.setPermissions(listPermissions);
        }
        return this.roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role fetchById(long id) {
        Optional<Role> rOptional = this.roleRepository.findById(id);
        if (rOptional.isPresent()) {
            return rOptional.get();
        } else {
            return null;
        }
    }

    public Role update(Role reqRole) {
        Role curRole = this.fetchById(reqRole.getId());
        if (curRole != null) {
            if (reqRole.getPermissions() != null) {
                List<Long> reqPermissions = reqRole.getPermissions()
                        .stream().map(x -> x.getId())
                        .collect(Collectors.toList());

                List<Permission> listPermissions = this.permissionRepository.findByIdIn(reqPermissions);

                curRole.setPermissions(listPermissions);
            }
            curRole.setName(reqRole.getName());
            curRole.setDescription(reqRole.getDescription());
            curRole.setActive(curRole.isActive());
            curRole = this.roleRepository.save(curRole);
        }

        return curRole;
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> specification, Pageable pageable) {
        Page<Role> pageSRole = this.roleRepository.findAll(specification, pageable);

        ResultPaginationDTO rsDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSRole.getTotalPages());
        meta.setTotal(pageSRole.getTotalElements());

        rsDTO.setMeta(meta);
        rsDTO.setResult(pageSRole.getContent());
        return rsDTO;
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
