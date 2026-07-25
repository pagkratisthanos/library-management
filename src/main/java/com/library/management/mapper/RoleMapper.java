package com.library.management.mapper;

import com.library.management.dto.RoleReadOnlyDTO;
import com.library.management.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleReadOnlyDTO mapToRoleReadOnlyDTO(Role role) {
        return new RoleReadOnlyDTO(
                role.getId(),
                role.getName()
        );
    }
}