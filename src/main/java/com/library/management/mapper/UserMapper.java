package com.library.management.mapper;

import com.library.management.dto.UserReadOnlyDTO;
import com.library.management.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().getName()
        );
    }
}