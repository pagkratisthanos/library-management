package com.library.management.api;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserReadOnlyDTO;
import com.library.management.mapper.UserMapper;
import com.library.management.model.User;
import com.library.management.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
}