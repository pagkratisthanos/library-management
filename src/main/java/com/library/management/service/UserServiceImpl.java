package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.UserFilters;
import com.library.management.dto.PasswordChangeDTO;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserPasswordUpdateDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import com.library.management.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.library.management.dto.UserRoleUpdateDTO;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class, EntityInvalidArgumentException.class})
    public User saveUser(UserInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        try {
            if (userRepository.findByUsername(dto.username()).isPresent()) {
                throw new EntityAlreadyExistsException("User", "User with username=" + dto.username() + " already exists");
            }

            Role role = roleRepository.findById(dto.roleId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Role", "Role with id=" + dto.roleId() + " does not exist"));

            User user = new User();
            user.setUsername(dto.username());
            user.setPassword(passwordEncoder.encode(dto.password()));
            role.addUser(user);

            User savedUser = userRepository.save(user);
            log.info("User with username={} saved successfully", dto.username());
            return savedUser;

        } catch (EntityAlreadyExistsException e) {
            log.error("Save failed. User with username={} already exists", dto.username());
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Save failed. Invalid arguments for user with username={}", dto.username());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUuid(UUID uuid) throws EntityNotFoundException {
        try {
            User user = userRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + uuid + " not found"));
            log.debug("User with uuid={} found successfully", uuid);
            return user;
        } catch (EntityNotFoundException e) {
            log.error("Get failed. User with uuid={} not found", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException {
        try {
            User user = userRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + uuid + " not found"));
            log.debug("Active user with uuid={} found successfully", uuid);
            return user;
        } catch (EntityNotFoundException e) {
            log.error("Get failed. Active user with uuid={} not found", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public void deleteUserByUuid(UUID uuid) throws EntityNotFoundException {
        try {
            User user = userRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + uuid + " not found"));

            user.softDelete();
            userRepository.save(user);
            log.info("User with uuid={} deleted successfully", uuid);

        } catch (EntityNotFoundException e) {
            log.error("Delete failed. User with uuid={} not found", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(UserFilters filters, Pageable pageable) {
        Page<User> users = userRepository.findAll(UserSpecification.build(filters), pageable);
        log.info("Get all users returned page={} size={}", users.getNumber(), users.getSize());
        return users;
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public User updateUserRole(UUID uuid, UserRoleUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        try {
            User user = userRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + uuid + " not found"));

            Role newRole = roleRepository.findById(dto.roleId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Role", "Role with id=" + dto.roleId() + " does not exist"));

            Role currentRole = user.getRole();

            if (currentRole != null && currentRole.getId().equals(newRole.getId())) {
                log.debug("User with uuid={} already has role={}", uuid, newRole.getName());
                return user;
            }

            boolean losesAdmin = currentRole != null
                    && ADMIN_ROLE.equals(currentRole.getName())
                    && !ADMIN_ROLE.equals(newRole.getName());

            if (losesAdmin && userRepository.countByRoleNameAndDeletedFalse(ADMIN_ROLE) <= 1) {
                throw new EntityInvalidArgumentException("User", "Cannot demote the last remaining administrator");
            }

            user.setRole(newRole);

            User updatedUser = userRepository.save(user);
            log.info("User with uuid={} moved to role={}", uuid, newRole.getName());
            return updatedUser;

        } catch (EntityNotFoundException e) {
            log.error("Role change failed. User with uuid={} not found", uuid);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Role change failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public User updateUserPassword(UUID uuid, UserPasswordUpdateDTO dto)
            throws EntityNotFoundException {
        try {
            User user = userRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + uuid + " not found"));

            user.setPassword(passwordEncoder.encode(dto.password()));

            User updatedUser = userRepository.save(user);
            log.info("Password changed for user with uuid={}", uuid);
            return updatedUser;

        } catch (EntityNotFoundException e) {
            log.error("Password change failed. User with uuid={} not found", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public User changeOwnPassword(String username, PasswordChangeDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        try {
            User user = userRepository.findByUsernameAndDeletedFalse(username)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with username=" + username + " not found"));

            if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
                throw new EntityInvalidArgumentException("User", "The current password is not correct");
            }

            if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
                throw new EntityInvalidArgumentException("User", "The new password must be different from the current one");
            }

            user.setPassword(passwordEncoder.encode(dto.newPassword()));

            User updatedUser = userRepository.save(user);
            log.info("User with username={} changed their own password", username);
            return updatedUser;

        } catch (EntityNotFoundException e) {
            log.error("Password change failed. User with username={} not found", username);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Password change failed for username={}. {}", username, e.getMessage());
            throw e;
        }
    }
}