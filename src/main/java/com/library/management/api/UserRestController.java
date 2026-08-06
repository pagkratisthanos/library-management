package com.library.management.api;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.UserFilters;
import com.library.management.dto.PasswordChangeDTO;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserReadOnlyDTO;
import com.library.management.mapper.UserMapper;
import com.library.management.model.User;
import com.library.management.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.library.management.dto.UserPasswordUpdateDTO;
import com.library.management.dto.UserRoleUpdateDTO;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Save a user")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<UserReadOnlyDTO> saveUser(@Valid @RequestBody UserInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        User savedUser = userService.saveUser(dto);
        UserReadOnlyDTO responseDTO = userMapper.mapToUserReadOnlyDTO(savedUser);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Get a user by uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getUser(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        User user = userService.getUserByUuidDeletedFalse(uuid);
        return ResponseEntity.ok(userMapper.mapToUserReadOnlyDTO(user));
    }

    @Operation(summary = "Delete a user")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        userService.deleteUserByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<UserReadOnlyDTO>> getAllUsers(
            @ModelAttribute UserFilters filters,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<User> users = userService.getAllUsers(filters, pageable);
        return ResponseEntity.ok(users.map(userMapper::mapToUserReadOnlyDTO));
    }

    @Operation(summary = "Change the password of the signed in user")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/me/password")
    public ResponseEntity<UserReadOnlyDTO> changeOwnPassword(
            Principal principal,
            @Valid @RequestBody PasswordChangeDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        User user = userService.changeOwnPassword(principal.getName(), dto);
        return ResponseEntity.ok(userMapper.mapToUserReadOnlyDTO(user));
    }

    @Operation(summary = "Change a user's role")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}/role")
    public ResponseEntity<UserReadOnlyDTO> updateUserRole(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserRoleUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        User user = userService.updateUserRole(uuid, dto);
        return ResponseEntity.ok(userMapper.mapToUserReadOnlyDTO(user));
    }

    @Operation(summary = "Reset a user's password")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}/password")
    public ResponseEntity<UserReadOnlyDTO> updateUserPassword(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserPasswordUpdateDTO dto)
            throws EntityNotFoundException {
        User user = userService.updateUserPassword(uuid, dto);
        return ResponseEntity.ok(userMapper.mapToUserReadOnlyDTO(user));
    }
}