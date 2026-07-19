package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.UserInsertDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}