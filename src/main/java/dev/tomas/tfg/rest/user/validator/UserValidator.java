package dev.tomas.tfg.rest.user.validator;

import dev.tomas.tfg.rest.user.exceptions.UserNotFoundException;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User validateUserExists(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
