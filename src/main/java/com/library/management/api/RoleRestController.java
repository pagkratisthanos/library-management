package com.library.management.api;

import com.library.management.dto.RoleReadOnlyDTO;
import com.library.management.mapper.RoleMapper;
import com.library.management.model.Role;
import com.library.management.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final IRoleService roleService;
    private final RoleMapper roleMapper;

    @Operation(summary = "Get all roles")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<RoleReadOnlyDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles.stream()
                .map(roleMapper::mapToRoleReadOnlyDTO)
                .toList());
    }
}